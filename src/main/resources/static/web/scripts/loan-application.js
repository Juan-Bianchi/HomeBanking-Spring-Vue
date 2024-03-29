const {createApp} = Vue;


createApp({
    data(){
        return{
            client: undefined,
            loans: [],
            accounts: [],
            account: undefined,
            genericLoans: [],
            windowWidth: window.innerWidth,
            activeChecks: ["Mortgage", "Personal", "Automotive"],
            filteredLoans: [],
            typeOfLoans: [],
            totalPages: 1,
            pageNumber: 1,
            visibleLoans: undefined,
            navNumbersArray: [],
            currentNavModulus: 0,
            numbersModulus: undefined,
            visibleNavNumbers: [],
            emptyLinesAmount: undefined,
            availableAmounts: [],
            stringAmount: "",
            loanApplicationDTO: {
                idLoan: undefined,
                amount: undefined,
                payments: undefined,
                associatedAccountNumber: undefined,
            },
            localStorage: [],
            notifCounter: 0,
        }
    },

    created(){
        this.loadData();
    },
    
    mounted(){
        window.addEventListener('resize', this.onResize);
    },

    methods: {

        // WHEN CREATED (TO RENDER PAGE)

        loadData: function(){
            let ownLoans = axios.get('/api/clients/current');
            let genericLoans = axios.get('/api/loans');
            let ownAccounts = axios.get('/api/clients/current/activeAccounts')
            Promise.all([ownLoans, genericLoans, ownAccounts])
                   .then(response => {    
                        this.client = {... response[0].data};
                        this.loans = this.client.loans.map(loan => ({... loan})).sort((l1, l2) => (l1.id > l2.id ? 1: -1));
                        this.genericLoans = [... response[1].data].sort((l1, l2)=> l1.id - l2.id)
                        this.accounts = [... response[2].data].sort((a1, a2)=> a1.id - a2.id)
                        let data = localStorage.getItem('notif');
                            if(data){
                                this.localStorage = JSON.parse(localStorage.getItem('notif'));
                            }
                            this.notifCounter = this.localStorage.filter(element => element.isRead == false).length;                                 
                        this.manageData();
                   })
                   //.catch(err => console.error(err.message));
        },

        manageData: function(){
            this.filterLoans();
            this.createTypeOfLoansList();
            this.showAvailableAmounts();
        },

        createTypeOfLoansList: function(){
            this.typesOfLoans = [... new Set(this.genericLoans.map(loan => loan.name))];
        },

        filterLoans: function(){
            this.filteredLoans = this.loans.filter(loan => this.activeChecks.some(category => category.startsWith(loan.name)));
            this.renderLoans();
        },

        showAvailableAmounts: function(){
            let mortgage = this.loans.filter(loan => loan.name.toLowerCase().includes('mortgage')).reduce(((total, loan)=> total + loan.amount),0);
            let personal = this.loans.filter(loan => loan.name.toLowerCase().includes('personal')).reduce(((total, loan)=> total + loan.amount),0);
            let automotive = this.loans.filter(loan => loan.name.toLowerCase().includes('automotive')).reduce(((total, loan)=> total + loan.amount),0);
            let availableMort = this.genericLoans.find(loan=> loan.name.includes('Mortgage')).maxAmount - mortgage;
            let availablePers = this.genericLoans.find(loan=> loan.name.includes('Personal')).maxAmount - personal;
            let availableAuto = this.genericLoans.find(loan=> loan.name.includes('Automotive')).maxAmount - automotive;

            if(availableMort > 10000){
                this.availableAmounts.push(({
                    id: this.genericLoans.find(loan=> loan.name.includes('Mortgage')).id,
                    name: 'Mortgage',
                    available: availableMort,
                }))
            }
            
            if(availablePers > 10000){
                this.availableAmounts.push(({
                    id: this.genericLoans.find(loan=> loan.name.includes('Personal')).id,
                    name: 'Personal',
                    available: availablePers,
                }))
            }
            
            if(availableAuto > 10000){
                this.availableAmounts.push(({
                    id: this.genericLoans.find(loan=> loan.name.includes('Automotive')).id,
                    name: 'Automotive',
                    available: availableAuto,
                }))
            }
            
        },


        //current loans table
        renderLoans: function(){

            let size = this.filteredLoans.length;
            let counter = 0;
            let transactionsArray = [];

            while(counter < size){
                transactionsArray.push(this.filteredLoans.slice(counter, counter+=5));
            }

            this.totalPages = transactionsArray.length;
            if(this.totalPages === 1){
                this.pageNumber = 1;
            }
            this.visibleLoans = transactionsArray[this.pageNumber - 1];
            if(!this.visibleLoans){
                this.visibleLoans = [];
            }
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


        // WHEN MOUNTED

        //loan application form
        formatAmountInput: function(){
            if(Number.isNaN(Number(this.stringAmount.slice(3)))){                
                this.stringAmount = "";
                this.loanApplicationDTO.amount = 0;
            }
            else{
                console.log(this.stringAmount);
                this.loanApplicationDTO.amount = Number(this.stringAmount);
                this.stringAmount =  Number(this.stringAmount).toLocaleString("es-AR", {style:"currency",currency:"USD"});
            }
            
        },

        numberWhenUsing: function(){
            this.stringAmount = "";         
        },

        triggerConfirmApplicationModal: function(){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the loan application',
                text: "If you accept the loan will be applied. If you want to cancel the request, just click 'Close' button.",
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                confirmButtonText: 'Accept',
                cancelButtonText: 'Close',
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/loans', { idLoan: this.loanApplicationDTO.idLoan, amount: this.loanApplicationDTO.amount, payments: this.loanApplicationDTO.payments, associatedAccountNumber: this.loanApplicationDTO.associatedAccountNumber,})
                    .then(response => {
                        console.log([response]);
                        this.localStorage.push({
                            number: response.data.id,
                            description: `A ${response.data.name} loan with amount U$S ${response.data.amount} has been transfered to your account.`,
                            isRead: false,
                            isDeleted: false,
                        })
                        localStorage.removeItem('notif');
                        localStorage.setItem('notif', JSON.stringify(this.localStorage));
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Loan applied!",
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
                           text: `The loan application was not completed: ${err.message.includes('403')? err.response.data: "There was an error in the loan request."}`,
                       })
                    })
                }
              })
        },

        applyLoan: function(){
            
        },

        //to render
        onResize(event) {
            this.windowWidth = screen.width
        },

        chooseAccount: function(destinantion){
            let template ="";
            this.accounts.forEach(account => {
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
                            window.location.href = `/web/account.html?id=${this.accounts.find(account => account.number.includes(this.account)).id}`
                        }
                    }
                })
        },


        //LOGOUT
        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "/web/index.html";
            })
        },

    }
}).mount("#app")