const {createApp} = Vue;

createApp( {
    data(){
        return{
            client: undefined,
            windowWidth: window.innerWidth,
            accounts: [],
            account: undefined,
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
            accountType: undefined,
            localStorage: [],
            notifCounter: 0,
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
            let client = axios.get('/api/clients/current')
            let accounts = axios.get('/api/clients/current/activeAccounts')
            Promise.all([client, accounts]).then(response => {
                    this.client = {... response[0].data};
                    this.accounts = response[1].data.map(account => account);
                    this.loans = this.client.loans.map(loan => loan);
                    this.totalBalance = this.accounts.reduce((total, actual)=> total + actual.balance, this.totalBalance);
                    this.manageData();
                    let data = localStorage.getItem('notif');
                    if(data){
                        this.localStorage = JSON.parse(localStorage.getItem('notif'));
                    }
                    this.notifCounter = this.localStorage.filter(element => element.isRead == false).length;
                })
                //.catch(err => console.error(err.message));
                 
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
            this.renderLoans();
        },

        goToPage: function(page){
            this.pageNumber = page;
            this.renderLoans();
        },

        manageNotifications: function(){
            this.localStorage = JSON.parse(localStorage.getItem('notif'));
            let template ="";
            if(this.localStorage){
                this.localStorage.forEach(element => {
                    template +=
                       `<tr>                        
                           <td>${element.number}</td>
                           <td style="width: 40%">${element.description}</td>
                           <td>
                               <div class="form-check form-check-inline seen">
                                   <input class="form-check-input check-trans" type="checkbox" id="${element.number}" value="${element.number}">
                                   <label class="form-check-label" for="${element.number}"> </label>
                               </div>
                           </td>
                           <td>
                               <div class="form-check form-check-inline delete">
                                   <input class="form-check-input check-trans" type="checkbox" id="${element.number}" value="${element.number}">
                                   <label class="form-check-label" for="${element.number}"> </label>
                               </div>
                           </td>                
                       </tr>`;
                       
                   });
            
           
                Swal.fire({
                    customClass: 'modal-sweet-alert',
                    title: 'To mark as seen or to delete just click the checkboxes.',
                    html:
                        `<div class="d-flex justify-content-between align-items-start">
                            <div class="container table-loan" v-if="visibleLoans.length">
                                <div class="row d-flex justify-content-center">
                                    <div class="col-11 col-md-12 col-lg-12">
                                        <div class="panel panelAccounts">
                                            <div class="panel-body table-responsive">
                                                <table class="table">
                                                    <thead>
                                                        <tr>
                                                            <th>Item</th>
                                                            <th>Description</th>
                                                            <th style= "width: 20%">Read</th> 
                                                            <th style= "width: 20%">Delete</th>                                             
                                                        </tr>
                                                    </thead>
                                                    <tbody>` +
                                                        template +                                                                
                                                    `</tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div> 
                        </div>`,   
                    width: 700,                             
                    showCloseButton: true,
                    showCancelButton: true,
                    cancelButtonColor: '#d33',
                    cancelButtonText: 'Close',
                    confirmButtonText: 'Accept'
                }).then((result) => {
                        seen = [ ...document.querySelectorAll('.swal2-container .seen input[type="checkbox"]:checked')];
                        del = [ ...document.querySelectorAll('.swal2-container .delete input[type="checkbox"]:checked')];
                        if (result.isConfirmed) {
                            this.notifCounter = this.localStorage.length - seen.length;
                            if(seen.length){
                                this.localStorage = this.localStorage.map( element => {
                                    seen.forEach(el => {
                                        if(el.value == element.number){
                                            element.isRead = true;
                                        }
                                    })
                                    return element;
                                });
                            }
                            
                            this.notifCounter = this.localStorage.filter(element => element.isRead == false).length;
                            if(del.length){
                                this.localStorage = this.localStorage.map(element => {
                                    del.forEach(el => {
                                        if(el.value == element.number){
                                            element.isDeleted = true;
                                        }
                                    })
                                    return element;
                                }).filter(element => element.isDeleted == false);
                            }

                            localStorage.removeItem('notif');
                            if(this.localStorage.length){
                                localStorage.setItem('notif', JSON.stringify(this.localStorage));
                            }
                            this.notifCounter = this.localStorage.filter(element => element.isRead == false).length;
                        }
                    })
            }
        },


        //WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width
        },

        chooseAccount: function(destinantion){
            let template ="";
            this.orderedAccounts.forEach(account => {
             template +=`<input class="form-check-input me-2" type="radio" name="account" id="${account.number}" value="${account.number}">
                 <label class="form-check-label me-4" for=${account.number}>
                    ${account.number}
                </label>`;
                
            });
            Swal.fire({
                customClass: 'modal-sweet-alert',
                icon: 'warning',
                title: 'Please select an account.',
                html:
                    '<div>' +
                        template +
                    '</div>',

                showCloseButton: true,
                showCancelButton: true,
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                    account = [ ...document.querySelectorAll('.swal2-container input[name="account"]')].find(element => element.checked);
                    this.account = account.value;
                    if (result.isConfirmed) {
                        if(destinantion.includes('transfer')){
                            window.location.href = `/web/transfers.html?number=${this.account}`
                        }
                        else{
                            window.location.href = `/web/account.html?id=${this.orderedAccounts.find(account => account.number.includes(this.account)).id}`
                        }
                    }
                })
        },

        createAccount(){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                icon: 'warning',
                title: 'Please select the type of your new account',
                html:
                    '<div>' +
                        '<input class="form-check-input me-2" type="radio" name="accountType" id="saving" value="SAVINGS" checked>' +
                        '<label class="form-check-label me-4" for="saving">' +
                            'Savings Account' +
                        '</label>' +

                        '<input class="form-check-input me-2" type="radio" name="accountType" id="current" value="CURRENT">' +
                        '<label class="form-check-label" for="current">' +
                            'Current Account' +
                        '</label>' +
                    '</div>',

                showCloseButton: true,
                showCancelButton: true,
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                accountType = [... document.querySelectorAll('.swal2-container input[name="accountType"]')].find(element => element.checked);
                this.accountType = accountType.value;
                if (result.isConfirmed) {
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
                            axios.post(`/api/clients/current/accounts`,`accountType=${this.accountType}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                            .then(response => {
                                this.localStorage.push({
                                    number: response.data.number,
                                    description: `A ${response.data.type.slice(0,1) + response.data.type.slice(1).toLowerCase()} account with number ${response.data.number} has been created.`,
                                    isRead: false,
                                    isDeleted: false,
                                })
                                localStorage.removeItem('notif');
                                localStorage.setItem('notif', JSON.stringify(this.localStorage));
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
                                text: err.message.includes('403')? err.response.data: err.message,
                            })
                            })
                        }
                    })
                }
            })
        },

        cancelAccount: function(account){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the account cancellation',
                text: `If you accept the account ${account.number} will be cancelled. You will not be able to make future transactions with the account, and it will not be available in your homebanking anymore. If you want to cancel the request, just click 'Close' button.`,
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                if (result.isConfirmed) {
                    account.isActive = false;
                    axios.patch('/api/clients/current/accounts',`number=${account.number}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response => {
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Account cancelled",
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
                           text: err.message.includes('403')? err.response.data: err.message,
                       })
                    })
                }
              })
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
                    window.location.href = "/web/index.html";
            })
        },

    },

}).mount("#app");