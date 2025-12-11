// products.js

// Save or Update Product
async function saveProduct(e) {
    e.preventDefault();
    const id = document.getElementById('id').value;
    const payload = {
        sku: document.getElementById('sku').value,
        name: document.getElementById('name').value,
        sellPrice: parseFloat(document.getElementById('sellPrice').value),
        mrp: parseFloat(document.getElementById('mrp').value),
        gstRate: parseInt(document.getElementById('gstRate').value),
        stock: parseInt(document.getElementById('stock').value),
        unit: document.getElementById('unit').value,
        description: document.getElementById('description').value,
        storeDate: document.getElementById('storeDate').value || null,
        expiryDate: document.getElementById('expiryDate').value || null,
        active: true
    };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/products/${id}` : '/api/products';
    const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) location.reload();
}

document.getElementById('productForm').addEventListener('submit', saveProduct);

// Edit Product
async function editProduct(btn) {
    const id = btn.getAttribute('data-id');
    const res = await fetch(`/api/products/${id}`);
    const p = await res.json();
    document.getElementById('id').value = p.id;
    document.getElementById('sku').value = p.sku;
    document.getElementById('name').value = p.name;
    document.getElementById('sellPrice').value = p.sellPrice;
    document.getElementById('mrp').value = p.mrp;
    document.getElementById('gstRate').value = p.gstRate;
    document.getElementById('stock').value = p.stock;
    document.getElementById('unit').value = p.unit;
    document.getElementById('description').value = p.description || '';
    document.getElementById('storeDate').value = p.storeDate || '';
    document.getElementById('expiryDate').value = p.expiryDate || '';
    const modal = new bootstrap.Modal(document.getElementById('productModal'));
    modal.show();
}

// Delete Product
async function deleteProduct(btn) {
    if (!confirm("Are you sure you want to delete this product?")) return;
    const id = btn.getAttribute('data-id');
    const res = await fetch(`/api/products/${id}`, { method: 'DELETE' });
    if (res.ok) location.reload();
}

// Print Barcode
function printBarcode(code) {
    const imgSrc = `/barcodes/${code}.png`;
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`<img src="${imgSrc}" onload="window.print();window.close();" />`);
}

// Table Button Event Delegation
document.querySelector('table').addEventListener('click', function (e) {
    const btn = e.target;

    if (btn.classList.contains('btn-danger')) deleteProduct(btn);
    if (btn.classList.contains('btn-outline-primary')) printBarcode(btn.getAttribute('data-barcode'));
    if (btn.classList.contains('btn-secondary')) editProduct(btn);
});

// Filters
function applyFilters() {
    const searchText = document.getElementById('productFilter').value.toLowerCase();
    const stockFilter = document.getElementById('stockFilter').value;
    const storeDateFilter = document.getElementById('filterStoreDate').value;
    const expiryDateFilter = document.getElementById('filterExpiryDate').value;

    document.querySelectorAll('tbody tr').forEach(tr => {
        const text = tr.innerText.toLowerCase();
        const stock = parseInt(tr.children[4].innerText);
        const storeDate = tr.children[6].innerText;
        const expiryDate = tr.children[7].innerText;

        let visible = text.includes(searchText);

        if (stockFilter === 'low') visible = visible && stock < 10;
        if (stockFilter === 'medium') visible = visible && stock >= 10 && stock <= 50;
        if (stockFilter === 'high') visible = visible && stock > 50;

        if (storeDateFilter) visible = visible && storeDate === storeDateFilter;
        if (expiryDateFilter) visible = visible && expiryDate === expiryDateFilter;

        tr.style.display = visible ? '' : 'none';
    });
}

document.getElementById('productFilter').addEventListener('input', applyFilters);
document.getElementById('stockFilter').addEventListener('change', applyFilters);
document.getElementById('filterStoreDate').addEventListener('change', applyFilters);
document.getElementById('filterExpiryDate').addEventListener('change', applyFilters);

// Download PDF
document.getElementById('downloadPdf').addEventListener('click', () => {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF('p', 'pt', 'a4');
    doc.setFontSize(14);
    doc.text('Smart Billing - Product List', 40, 40);

    const rows = [];
    document.querySelectorAll('tbody tr').forEach(tr => {
        if (tr.style.display !== 'none') {
            rows.push(Array.from(tr.querySelectorAll('td')).map(td => td.innerText.trim()));
        }
    });

    doc.autoTable({
        head: [['SKU', 'Name', 'Price', 'GST', 'Stock', 'Unit', 'Store Date', 'Expiry Date']],
        body: rows.map(row => row.slice(0, 8)), // remove the last column (Barcode)
        startY: 60,
    });

    doc.save('products.pdf');
});