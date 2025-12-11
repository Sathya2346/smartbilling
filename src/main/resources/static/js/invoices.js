// Filter Invoices
document.getElementById('invoiceFilter').addEventListener('input', () => {
    const filter = document.getElementById('invoiceFilter').value.toLowerCase();
    document.querySelectorAll('tbody tr').forEach(tr => {
        const invoiceNo = tr.children[0].innerText.toLowerCase();
        const customer = tr.children[1].innerText.toLowerCase();
        tr.style.display = (invoiceNo.includes(filter) || customer.includes(filter)) ? '' : 'none';
    });
});

// Download PDF
document.getElementById('downloadPdf').addEventListener('click', () => {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF('p','pt','a4');
    doc.setFontSize(14);
    doc.text('Invoice List - Smart Billing', 40, 40);

    const rows = [];
    document.querySelectorAll('tbody tr').forEach(tr => {
        if(tr.style.display !== 'none') {
            const row = Array.from(tr.children)
                .slice(0, tr.children.length - 1)  // Exclude last column (Actions)
                .map(td => td.innerText.trim());
            rows.push(row);
        }
    });

    doc.autoTable({
        head: [['No','Customer','Subtotal','GST','Total','Mode']], // same as columns excluding Actions
        body: rows,
        startY: 60
    });

    doc.save('invoices.pdf');
});