const {createApp} = Vue;

createApp( {
    data(){
        return{
            client: undefined,
            windowWidth: window.innerWidth,
            accounts: [],
            orderedAccounts: [],
            loans: [],
            orderedLoans: [],

        }
    },

    created() {
        this.loadData();
    },

    mounted() {
        window.addEventListener('resize', this.onResize);
        
    },

    methods: {
        loadData(){
            axios.get("http://localhost:8080/api/clients/1")
                 .then(response => {
                    this.client = {... response.data};
                    this.accounts = this.client.accounts.map(account => account);
                    this.loans = this.client.loans.map(loan => loan);
                    this.createPieChart();
                    this.orderAccounts();
                    this.orderLoans();
                 })
                 .catch(err => console.error(err.message));
        },

        onResize(event) {
            this.windowWidth = screen.width
        },

        createPieChart() {

            let serie = [... this.accounts.map(account => account.balance)];
            let label = [... this.accounts.map(account => account.number)];

            console.log(serie);
            console.log(label);
            console.log(this.accounts);

            let options = {
                series: serie,

                chart: {
                    width: 380,
                    type: 'pie',
                },

                labels: label,
              
                colors: (function(length){
                        let arrayColor =[];
                        let colors = ['#d39200', '#06283D', 'red'];
                        for(let i=0; i < length; i++){
                            arrayColor.push(colors[i]);
                        }
                        return arrayColor;
                    }(this.accounts.length)),

                fill: {
                    colors: (function(length){
                        let arrayColor =[];
                        let colors = ['#d39200', '#06283D', 'red'];
                        for(let i=0; i < length; i++){
                            arrayColor.push(colors[i]);
                        }
                        return arrayColor;
                    }(this.accounts.length)),
                },

                responsive: [{
                    breakpoint: 480,
                    options: {
                        chart: {
                            width: 200
                        },
                        
                        legend: {
                            position: 'bottom'
                        }
                    }
                }]
            };
      
            let chart = new ApexCharts(document.querySelector("#totalHavingsChart"), options);
            chart.render();
        },

        orderAccounts: function(){
            this.orderedAccounts = this.accounts.map(account => ({... account}));
            this.orderedAccounts.sort((a1, a2) => { return a1.id > a2.id ? 1: -1; });
        },

        orderLoans: function(){
            this.orderedLoans = this.loans.map(loan => ({... loan}));
            this.orderedLoans.sort((a1, a2) => { return a1.id > a2.id ? 1: -1; });
        },

    },

}).mount("#app");