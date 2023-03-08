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
            totalBalance: 0,
            currentAccount: undefined,
            genericLoans: undefined,
        }
    },
    created() {
        this.loadData();
    },

    mounted() {
        window.addEventListener('resize', this.onResize);
    },

    methods: {

        //WHEN CREATED

        loadData(){
            axios.get("http://localhost:8080/api/clients/current")
                .then(response => {
                    this.client = {... response.data};
                    this.accounts = this.client.accounts.map(account => account);
                    this.loans = this.client.loans.map(loan => loan);
                    this.totalBalance = this.accounts.reduce((total, actual)=> total + actual.balance, this.totalBalance);
                    this.manageData();
                })
                .catch(err => console.error(err.message));
                 
        },

        //manage all methods
        manageData: function(){
            this.orderAccounts();
            this.orderLoans();
            if(this.totalBalance){
                this.createPieChart();
            }        
        },

        orderAccounts: function(){
            this.orderedAccounts = this.accounts.map(account => ({... account}));
            this.orderedAccounts.sort((a1, a2) => { return a1.id > a2.id ? 1: -1; });
            this.currentAccount = this.orderedAccounts[0];
        },

        orderLoans: function(){
            this.orderedLoans = this.loans.map(loan => ({... loan}));
            this.orderedLoans.sort((a1, a2) => { return a1.id > a2.id ? 1: -1; });
        },

        createPieChart() {

            let serie = this.orderedAccounts.map(account => account.balance);
            let label = this.orderedAccounts.map(account => account.number);

            let options = {
                series: serie,

                chart: {
                    width: 380,
                    type: 'pie',
                },

                labels: label,
              
                colors: (function(length){
                        let arrayColor =[];
                        let colors = ['#d39200', '#06283D', '#cd6aaa'];
                        for(let i=0; i < length; i++){
                            arrayColor.push(colors[i]);
                        }
                        return arrayColor;
                    }(this.accounts.length)),

                fill: {
                    colors: (function(length){
                        let arrayColor =[];
                        let colors = ['#d39200', '#06283D', '#cd6aaa'];
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


        //WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width
        },

        createAccount(){
            axios.post(`/api/clients/current/accounts`)
                 .then(response => {
                    location.reload();
                    console.log("account created");
                 })
                 .catch(err => console.error(err.message));
        },

        setCurrentAccount: function(movement){
            let carouselAccounts = document.getElementById("carousel-inner-accounts");
            let item = [... carouselAccounts.children].find(element => element.className.includes("carousel-item active"));
            let previousAccountNumber = item.innerText.split('\n')[0];
            let index = this.orderedAccounts.findIndex(account => account.number.includes(previousAccountNumber));
            if(movement.includes('previous')){
               this.currentAccount = index == 0? this.orderedAccounts[this.orderedAccounts.length - 1] : this.orderedAccounts[index - 1];
            }
            else{
                this.currentAccount = index == this.orderedAccounts.length - 1? this.orderedAccounts[0] : this.orderedAccounts[index + 1];
            }
        },

        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },

    },

}).mount("#app");