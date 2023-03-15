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
            totalPages: 1,
            pageNumber: 1,
            visibleLoans: [],
            navNumbersArray: [],
            visibleNavNumbers: [],
            emptyLinesAmount: undefined,
            currentNavModulus: 0,

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
            this.renderLoans();
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
                    width: 310,
                    type: 'donut',
                    foreColor: '#e6e5de',
                },

                plotOptions: {
                    pie: {
                      donut: {
                        labels: {
                          show: true, 
                        }
                      }
                    }
                },

                labels: label,

                dataLabels: {
                    enabled: true,
                },
              
                colors: (function(length){
                        let arrayColor =[];
                        let colors = ['#d39200', '#06283D', '#256D85'];
                        for(let i=0; i < length; i++){
                            arrayColor.push(colors[i]);
                        }
                        return arrayColor;
                    }(this.accounts.length)),

                fill: {
                    colors: (function(length){
                        let arrayColor =[];
                        let colors = ['#d39200', '#06283D', '#256D85'];
                        for(let i=0; i < length; i++){
                            arrayColor.push(colors[i]);
                        }
                        return arrayColor;
                    }(this.accounts.length)),
                },

                legend: {
                    position: 'bottom',
                    labels: {
                        colors: ['#ffffff', '#ffffff', '#ffffff'],
                        useSeriesColors: false
                    },
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

        renderLoans: function(){

            let size = this.orderedLoans.length;
            let counter = 0;
            let transactionsArray = [];

            while(counter < size){
                transactionsArray.push(this.orderedLoans.slice(counter, counter+=5));
            }

            this.totalPages = transactionsArray.length;
            if(this.totalPages === 1){
                this.pageNumber = 1;
            }
            this.visibleLoans = transactionsArray[this.pageNumber - 1];
            
            let numbers = [];
            for(let i = 1; i <= this.totalPages; i++){
                numbers.push(i);
            }
            counter = 0;
            this.navNumbersArray = [];
            while(counter < this.totalPages){
                this.navNumbersArray.push( numbers.slice(counter, counter+=3) );
            }
            this.visibleNavNumbers = this.navNumbersArray[this.currentNavModulus];
            this.emptyLinesAmount = 5 - this.visibleLoans.length;
        },
        
        changePage: function(movement){
            this.pageNumber += movement;
            this.currentNavModulus = Math.floor((this.pageNumber - 1) / 3);
            console.log(this.currentNavModulus);
            this.renderLoans();
        },

        goToPage: function(page){
            this.pageNumber = page;
            this.renderLoans();
        },


        //WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width
        },

        createAccount(){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the account creation',
                text: "If you accept the account will be created. If you want to cancel the request, just click 'Close' button.",
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post(`/api/clients/current/accounts`)
                    .then(response => {
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Account created!",
                            icon: 'success',
                            confirmButtonText: 'Accept'
                        }).then((result) => {
                            location.reload();
                        })
                    })
                    .catch(err =>{
                       console.log([err])
           
                       Swal.fire({
                           customClass: 'modal-sweet-alert',
                           icon: 'error',
                           title: 'Oops...',
                           text: err.message,
                       })
                    })
                }
              })
        },

        setCurrentAccount: function(movement){
            let carouselAccounts = document.getElementById("carousel-inner-accounts");
            console.log([carouselAccounts]);
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

        slideCarousel: function(position){
            let carouselAccounts = document.getElementById("carousel-inner-accounts");
            const carousel = new bootstrap.Carousel(carouselAccounts);
            carousel.to(position);
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