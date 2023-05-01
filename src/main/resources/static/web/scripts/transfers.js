const {createApp} = Vue;


createApp({
    data(){
        return{
            client: undefined,
            barOpen: true,
            orderedAccounts: [],
            account: undefined,
            number: "",
            originAccount: "",
            isMyOwnAccount: undefined,
            destinationAccount: "",
            amount: "",
            amountNumber: null,
            description: "",
        }
    },

    created(){
        this.loadData();
    },

    mounted(){
        
    },

    methods:{

        manageData: function(){
             const urlString = location.search;
             const parameters = new URLSearchParams(urlString);
             this.number = parameters.get('number'); 
             this.originAccount = parameters.get('number');
             

        },

        loadData: function(){
            let client = axios.get('/api/clients/current');
            let accounts = axios.get('/api/clients/current/activeAccounts')
            Promise.all([client, accounts]).then(response => {
                    this.client = {... response[0].data};
                    this.orderedAccounts = [... response[1].data.map(account => ({... account}))];
                    this.orderedAccounts.sort((ac1, ac2)=>ac1.id - ac2.id);
                    this.manageData();
                 })
        },

        formatAmountInput: function(){
            if(Number.isNaN(Number(this.amount.slice(3)))){
                this.amount = "";
                this.amountNumber =  0;
            }
            else{
                this.amountNumber =  Number(this.amount);
                this.amount =  Number(this.amount).toLocaleString("es-AR", {style:"currency",currency:"USD"});
            }
            
        },

        numberWhenUsing: function(){    
            this.amount = "";
        },


        //METHODS USED WHEN MOUNTED

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

        toggleMenu: function(){
            let sidebar = document.getElementById("sidebar");
            let smallMenu = document.querySelector(".small-menu-transaction");
            let contentWrapper = document.querySelector(".transaction-wrapper");
            
            if(this.barOpen){
                sidebar.style.left = "-100vw";
                smallMenu.style.top = "4vh";
                contentWrapper.style.width = `${this.windowWidth - 280}px`;
                this.barOpen = false;
            }
            else {
                sidebar.style.left = "2vw";
                smallMenu.style.top = "-1000vh";
                contentWrapper.style.width = `${this.windowWidth - 280}px`;
                this.barOpen = true;
            }
            
        },

        completeTransfer: function(){
            if(Number.isNaN(Number(this.amountNumber)) || !this.amountNumber ){
                this.amountNumber = 0;
            }
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the transaction',
                text: "If you accept the transaction will be completed. If you want to cancel the request, just click 'Close' button.",
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/transactions', `amount=${this.amountNumber}&description=${this.description}&origAccountNumb=${this.originAccount}&destAccountNumb=${this.destinationAccount}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response => {
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Transaction completed!",
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
                           text: `The transfer was not completed: ${err.message.includes('400')? err.message: err.response.data}`,
                       })
                    })
                }
              })
        },

        logout(){
            this.client = undefined;
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "/web/index.html";
            })
        },
    },
}).mount("#app");