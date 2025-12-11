// =====================================================
// billing.js - Smart Billing
// =====================================================

// GLOBAL VARIABLES
let beepSound;
let zxingReader = null;
let scannerActive = false;
let lastScanned = "";
let cart = [];

// =====================================================
// LOCAL STORAGE FUNCTIONS
// =====================================================
function saveCart() {
    localStorage.setItem("smartBillingCart", JSON.stringify(cart));
}

function loadCart() {
    const saved = localStorage.getItem("smartBillingCart");
    if (saved) {
        cart = JSON.parse(saved);
        renderCart();
        updateSummary();
    }
}

function clearCart() {
    cart = [];
    localStorage.removeItem("smartBillingCart");
    renderCart();
    updateSummary();
}

// =====================================================
// INIT
// =====================================================
document.addEventListener("DOMContentLoaded", () => {
    beepSound = document.getElementById("beepSound");

    // Load saved cart from LocalStorage
    loadCart();

    // Show/hide UPI reference based on payment mode
    const paymentModeEl = document.getElementById("paymentMode");
    const upiSection = document.getElementById("upiSection");
    paymentModeEl.addEventListener("change", () => {
        if (paymentModeEl.value === "UPI") {
            upiSection.classList.remove("d-none");
        } else {
            upiSection.classList.add("d-none");
        }
    });
});

// =====================================================
// TOGGLE CAMERA
// =====================================================
function toggleCamera() {
    if (scannerActive) stopCamera();
    else startCamera();
}

// =====================================================
// START CAMERA SCANNING
// =====================================================
async function startCamera() {
    if (scannerActive) return;

    const status = document.getElementById("scannerStatus");
    status.textContent = "Starting camera...";

    try {
        zxingReader = new ZXing.BrowserMultiFormatReader();

        await zxingReader.decodeFromVideoDevice(
            null,
            "cameraPreview",
            (result, err) => {
                if (result) {
                    const barcode = result.getText();
                    if (barcode && barcode !== lastScanned) {
                        lastScanned = barcode;
                        onBarcodeScanned(barcode);
                    }
                }
            }
        );

        scannerActive = true;
        status.textContent = "Scanner active ✓";
        status.classList.replace("text-danger", "text-success");
        document.getElementById("cameraBtn").innerText = "Stop camera";
    } catch (err) {
        console.error("Camera error:", err);
        status.textContent = "Camera error!";
        status.classList.replace("text-success", "text-danger");
    }
}

// =====================================================
// STOP CAMERA
// =====================================================
function stopCamera() {
    if (!scannerActive) return;

    if (zxingReader) zxingReader.reset();
    scannerActive = false;
    lastScanned = "";

    const status = document.getElementById("scannerStatus");
    status.textContent = "Scanner stopped";
    status.classList.replace("text-success", "text-danger");
    document.getElementById("cameraBtn").innerText = "Start camera";
}

// =====================================================
// HANDLE BARCODE SCAN
// =====================================================
function onBarcodeScanned(barcode) {
    console.log("Scanned barcode:", barcode);

    // Play beep sound
    if (beepSound) beepSound.play().catch(() => {});

    // Fetch product info from backend
    fetchProduct(barcode);
}

// =====================================================
// FETCH PRODUCT BY BARCODE
// =====================================================
function fetchProduct(barcode) {
    const cleaned = barcode.trim();
    fetch(`/api/products/barcode?code=${encodeURIComponent(cleaned)}`)
        .then(res => {
            if (!res.ok) throw new Error("Product not found");
            return res.json();
        })
        .then(product => addProductToCart(product))
        .catch(() => alert("Product not found"));
}

// =====================================================
// ADD PRODUCT TO CART
// =====================================================
function addProductToCart(product) {
    let existing = cart.find(item => item.id === product.id);

    const cartItem = {
        id: product.id,
        barcode: product.barcode || "",
        name: product.name || "",
        price: parseFloat(product.sellPrice || product.price || 0),
        gst: parseInt(product.gstRate || product.gst || 0),
        qty: 1
    };

    if (existing) {
        existing.qty += 1;
    } else {
        cart.push(cartItem);
    }

    renderCart();
    updateSummary();
    saveCart();   // Save updated cart
}

// =====================================================
// RENDER CART TABLE
// =====================================================
function renderCart() {
    const tbody = document.getElementById("cartBody");
    tbody.innerHTML = "";

    cart.forEach(item => {
        const gstValue = (item.price * item.gst) / 100;
        const total = (item.price + gstValue) * item.qty;

        tbody.innerHTML += `
            <tr>
                <td>${item.barcode}</td>
                <td>${item.name}</td>
                <td>${item.qty}</td>
                <td>₹${item.price.toFixed(2)}</td>
                <td>₹${gstValue.toFixed(2)}</td>
                <td>₹${total.toFixed(2)}</td>
                <td><button class="btn btn-danger btn-sm" onclick="removeItem(${item.id})">X</button></td>
            </tr>
        `;
    });
}

// =====================================================
// REMOVE ITEM FROM CART
// =====================================================
function removeItem(productId) {
    cart = cart.filter(p => p.id !== productId);
    renderCart();
    updateSummary();
    saveCart();  // Save updated cart
}

// =====================================================
// UPDATE CART SUMMARY
// =====================================================
function updateSummary() {
    let subtotal = 0;
    let gstTotal = 0;

    cart.forEach(item => {
        const gstVal = (item.price * item.gst) / 100;
        subtotal += item.price * item.qty;
        gstTotal += gstVal * item.qty;
    });

    const discount = parseFloat(document.getElementById("discount").value || 0);
    const total = subtotal + gstTotal - discount;

    document.getElementById("subtotal").innerText = subtotal.toFixed(2);
    document.getElementById("gstTotal").innerText = gstTotal.toFixed(2);
    document.getElementById("discountView").innerText = discount.toFixed(2);
    document.getElementById("total").innerText = total.toFixed(2);
}

// =====================================================
// ADD PRODUCT BY MANUAL SKU
// =====================================================
function addSku() {
    const sku = document.getElementById("manualSku").value.trim();
    if (!sku) return;

    fetch(`/api/products/sku/${encodeURIComponent(sku)}`)
        .then(res => {
            if (!res.ok) throw new Error("Product not found");
            return res.json();
        })
        .then(product => addProductToCart(product))
        .catch(() => alert("Product not found"));

    document.getElementById("manualSku").value = "";
}

// =====================================================
// CHECKOUT FUNCTION
// =====================================================
function checkout() {
    if (cart.length === 0) {
        alert("Cart is empty!");
        return;
    }

    const customerId = document.getElementById("customer").value || null;
    const paymentMode = document.getElementById("paymentMode").value;
    const upiRef = document.getElementById("upiRef").value || null;
    const discount = parseFloat(document.getElementById("discount").value || 0);

    const items = cart.map(item => ({
        productId: item.id,
        qty: item.qty,
        unitPrice: item.price
    }));

    const payload = {
        customerId,
        paymentMode,
        upiReference: upiRef,
        discount,
        items
    };

    // If CASH — normal invoice
    if (paymentMode === "CASH") {
        fetch("/api/invoices", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(data => {
                clearCart();  // REMOVE FROM LOCAL STORAGE
                window.location.href = `/invoice-print?no=${data.invoiceNo}`;
            })
            .catch(err => alert("Error creating invoice: " + err));
    } else {
        // UPI / CARD — Razorpay flow
        fetch("/api/invoices/razorpay", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(data => {

                stopCamera();  // ⬅ STOP CAMERA WHEN RAZORPAY OPENS

                const options = {
                    key: "rzp_test_RTHhlZabuiuFWK",
                    amount: data.amount * 100,
                    currency: data.currency,
                    name: "Smart Billing",
                    description: "Invoice Payment",
                    order_id: data.razorpayOrderId,
                    handler: function (response) {
                        fetch("/api/invoices", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(payload)
                        })
                            .then(res => res.json())
                            .then(invoice => {
                                clearCart(); // REMOVE LOCAL STORAGE
                                window.location.href = `/invoice-print?no=${invoice.invoiceNo}`;
                            });
                    }
                };
                const rzp = new Razorpay(options);
                rzp.open();
            })
            .catch(err => alert("Error creating Razorpay order: " + err));
    }
}