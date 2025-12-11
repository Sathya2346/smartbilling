// Fetch invoices from backend API
async function fetchInvoices() {
    const res = await fetch('/api/invoices');
    if (!res.ok) throw new Error('Failed to fetch invoices');
    return await res.json();
}

function formatCurrency(value) {
    return '₹' + value.toFixed(2);
}

// Get last 7 days labels
function getLast7Days() {
    const days = [];
    for (let i = 6; i >= 0; i--) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        days.push(d.toISOString().slice(0,10)); // YYYY-MM-DD
    }
    return days;
}

// Render charts
async function renderCharts() {
    try {
        const invoices = await fetchInvoices();

        // Total revenue
        const totalRevenue = invoices.reduce((sum, inv) => sum + inv.total, 0);
        document.getElementById('totalRevenue').innerText = formatCurrency(totalRevenue);

        // Last 7 days revenue
        const last7Days = getLast7Days();
        const dailyRevenue = last7Days.map(day => {
            return invoices
                .filter(inv => inv.createdAt?.slice(0,10) === day)
                .reduce((sum, inv) => sum + inv.total, 0);
        });

        const weeklyCtx = document.getElementById('weeklyRevenueChart').getContext('2d');
        new Chart(weeklyCtx, {
            type: 'line',
            data: {
                labels: last7Days,
                datasets: [{
                    label: 'Revenue',
                    data: dailyRevenue,
                    borderColor: 'rgba(75,192,192,1)',
                    backgroundColor: 'rgba(75,192,192,0.2)',
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: true } },
                scales: {
                    y: { beginAtZero: true, ticks: { callback: val => formatCurrency(val) } }
                }
            }
        });

        // Revenue by payment method
        const paymentMap = {};
        invoices.forEach(inv => {
            const mode = inv.paymentMode || 'CASH';
            paymentMap[mode] = (paymentMap[mode] || 0) + inv.total;
        });

        const paymentCtx = document.getElementById('paymentChart').getContext('2d');
        new Chart(paymentCtx, {
            type: 'pie',
            data: {
                labels: Object.keys(paymentMap),
                datasets: [{
                    data: Object.values(paymentMap),
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.4)',
                        'rgba(54, 162, 235, 0.4)',
                        'rgba(255, 206, 86, 0.4)',
                        'rgba(75, 192, 192, 0.4)',
                        'rgba(153, 102, 255, 0.4)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                radius: '100%',
                layout: { padding: 5 },
                plugins: {
                    legend: { position: 'bottom' },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.label + ': ' + formatCurrency(context.raw);
                            }
                        }
                    }
                }
            }
        });

    } catch (err) {
        console.error('Error rendering charts:', err);
    }
}

// Run on page load
document.addEventListener('DOMContentLoaded', renderCharts);