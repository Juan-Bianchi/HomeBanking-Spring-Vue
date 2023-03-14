const {createApp} = Vue;


createApp({
    data(){
        return{
            client: undefined,
            barOpen: true,
            orderedAccounts: [],
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
            axios.get('/api/clients/current')
                 .then(response => {
                    this.client = {... response.data};
                    this.orderedAccounts = [... this.client.accounts.map(account => ({... account}))];
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
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },
    },
}).mount("#app");