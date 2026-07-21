import { createApp, ref } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';

createApp({
    setup() {
        const loading = ref(false);
        const dashboard = ref('-');
        const loopProxy = ref('-');
        const orderProxy = ref('-');

        const fetchJson = async (url) => {
            const res = await fetch(url);
            return res.json();
        };

        const loadDashboard = async () => {
            loading.value = true;
            try { dashboard.value = JSON.stringify(await fetchJson('/api/v1/dashboard'), null, 2); }
            finally { loading.value = false; }
        };
        const loadLoopProxy = async () => {
            loading.value = true;
            try { loopProxy.value = JSON.stringify(await fetchJson('/proxy/loop/trust'), null, 2); }
            finally { loading.value = false; }
        };
        const loadOrderProxy = async () => {
            loading.value = true;
            try { orderProxy.value = JSON.stringify(await fetchJson('/proxy/orders/1001'), null, 2); }
            finally { loading.value = false; }
        };

        return { loading, dashboard, loopProxy, orderProxy, loadDashboard, loadLoopProxy, loadOrderProxy };
    }
}).mount('#app');
