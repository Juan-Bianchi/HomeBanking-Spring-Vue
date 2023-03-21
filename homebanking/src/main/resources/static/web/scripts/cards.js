const {createApp} = Vue;

createApp({
    
    data(){
        return {
            client: undefined,
            orderedAccounts: [],
            account: undefined,
            cards: [],
            creditCards: [],
            debitCards: [],
            windowWidth: screen.width,
            showInfoCards : true,
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

    methods:{

        loadData: function(){
            let client = axios.get(`http://localhost:8080/api/clients/current`)
            let cards = axios.get('http://localhost:8080/api/clients/current/activeCards')
            let accounts = axios.get('http://localhost:8080/api/clients/current/activeAccounts')
            Promise.all([client, cards, accounts])
                    .then(response => {
                            this.client = {... response[0].data};
                            console.log(response[1].data);
                            this.orderedAccounts = response[2].data.sort((a1, a2) => { return a1.id > a2.id ? 1: -1; });
                            this.cards = response[1].data.map(card => ({... card,
                                         isExpired: new Date().getTime() > new Date(card.thruDate).getTime(), 
                                         isAboutToExpire: new Date(card.thruDate).getTime() - new Date().getTime() < 30 * 3600 * 24 * 1000 &&
                                                          new Date(card.thruDate).getTime() - new Date().getTime() > 0,
                                        })
                            );    
                            let data = localStorage.getItem('notif');
                            if(data){
                                this.localStorage = JSON.parse(localStorage.getItem('notif'));
                            }
                            this.notifCounter = this.localStorage.filter(element => element.isRead == false).length;                                 
                            this.manageData();
                            
                    })
        },

        manageData(){
            this.filterCards();

        },

        filterCards: function(){
            let credit = this.cards.filter(card => card.type.includes('CREDIT'));
            this.creditCards = credit.sort((c1, c2) => c2.color > c1.color? -1: 1);
            let debit = this.cards.filter(card => card.type.includes('DEBIT'));
            this.debitCards = debit.sort((c1, c2) => c2.color > c1.color? -1: 1);
        },


        splitNumber: function(number){
            const reg = /-/g;
            return number.replace(reg, ' ');
        },

        hideCardNumber: function(number){
            let splittedNumber = number.split('-');
            for(let i=0; i < 3; i++){
                splittedNumber[i] = '••••';
            }
            return splittedNumber.join(' ');
        },

        getCardDate: function(date){
            let arrayDate = date.split('-');
            return arrayDate[1] + "/" + arrayDate[0];
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
                        }
                    })
            }
        },
        

        // WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width;
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
                            window.location.href = `http://localhost:8080/web/transfers.html?number=${this.account}`
                        }
                        else{
                            window.location.href = `http://localhost:8080/web/account.html?id=${this.orderedAccounts.find(account => account.number.includes(this.account)).id}`
                        }
                    }
                })
        },

        canAddCreditCard: function(){
            let canAdd = false;
            let allowedCards = this.creditCards.filter(card => card.isAboutToExpire || card.isExpired);
            if(this.creditCards.length - allowedCards.length < 3){
                canAdd = true;
            }

            return canAdd;
        },

        canAddDebitCard: function(){
            let canAdd = false;
            let allowedCards = this.debitCards.filter(card => card.isAboutToExpire || card.isExpired);
            if(this.debitCards.length - allowedCards.length < 3){
                canAdd = true;
            }

            return canAdd;
        },

        cancelCard: function(card){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the card cancellation',
                text: `If you accept the card number ${card.number} will be cancelled. You will not be able to make future purchases with the card, and it will not be available in your homebanking anymore. If you want to cancel the request, just click 'Close' button.`,
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                if (result.isConfirmed) {
                    card.isActive = false;
                    axios.patch('/api/clients/current/cards',`cardNumber=${card.number}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response => {
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Card cancelled",
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

        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },

    },

}).mount('#app')