// Save / Update Customer
document.getElementById('customerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('id').value;
    const payload = {
        name: document.getElementById('name').value,
        phone: document.getElementById('phone').value,
        email: document.getElementById('email').value,
        address: document.getElementById('address').value
    };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/customers/${id}` : '/api/customers';
    const res = await fetch(url, {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    });
    if(res.ok) location.reload();
});

// Edit Customer
async function editCustomer(btn){
    const id = btn.getAttribute('data-id');
    const res = await fetch(`/api/customers`);
    const list = await res.json();
    const c = list.find(x=>x.id==id);
    document.getElementById('id').value = c.id;
    document.getElementById('name').value = c.name;
    document.getElementById('phone').value = c.phone || '';
    document.getElementById('email').value = c.email || '';
    document.getElementById('address').value = c.address || '';
    const modal = new bootstrap.Modal(document.getElementById('customerModal'));
    modal.show();
}

// Delete Customer
async function deleteCustomer(btn){
    const id = btn.getAttribute('data-id');
    if(confirm('Delete customer?')){
        const res = await fetch(`/api/customers/${id}`, {method:'DELETE'});
        if(res.ok) location.reload();
    }
}

// Filter Customers
document.getElementById('customerFilter').addEventListener('input', () => {
    const filter = document.getElementById('customerFilter').value.toLowerCase();
    document.querySelectorAll('tbody tr').forEach(tr=>{
        const name = tr.children[0].innerText.toLowerCase();
        const phone = tr.children[1].innerText.toLowerCase();
        const email = tr.children[2].innerText.toLowerCase();
        tr.style.display = (name.includes(filter) || phone.includes(filter) || email.includes(filter)) ? '' : 'none';
    });
});

// Download PDF
document.getElementById('downloadPdf').addEventListener('click', ()=>{
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF('p','pt','a4');
    doc.setFontSize(14);
    doc.text('Customer List - Smart Billing', 40, 40);
    const rows = [];
    document.querySelectorAll('tbody tr').forEach(tr=>{
        if(tr.style.display!=='none'){
            rows.push(Array.from(tr.children).slice(0,4).map(td=>td.innerText.trim())); // exclude Actions column
        }
    });
    doc.autoTable({
        head: [['Name','Phone','Email','Address']],
        body: rows,
        startY:60
    });
    doc.save('customers.pdf');
});