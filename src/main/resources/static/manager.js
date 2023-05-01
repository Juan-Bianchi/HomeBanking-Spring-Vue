const {createApp} = Vue;

createApp({
    data() {
        return{
            firstName: "",
            lastName: "",
            email:"",
            password: "",
            loansJSON: undefined,
            clients : [],
            clientChange:{},
            loans : [],
            loan: {
                name: undefined,
                maxAmount: undefined,
                payments: [],
                interestRate: undefined,
            },
            noName: false,
            noMaxAmount: false,
            noPayments: false,
            noInterestRate: false,
        }

    
    },

    created() {
        this.loadData();
    },


    methods: {
        loadData: function(){  
            let clients = axios.get("http://localhost:8080/api/clients")
            let loans = axios.get('http://localhost:8080/api/loans')
            Promise.all([clients, loans])
                   .then(response => {
                        this.loansJSON = response[1].data;
                        console.log(this.loansJSON);
                        this.clients = response[0].data.map(client => ({... client}));
                        console.log(this.clients);
                        this.loans = response[1].data.map(loan => ({... loan}));

                 })
                 .catch(err => console.error(err.message));
        },

        addClient() {
           
            if( this.lastName !== "" && this.firstName !== "" && this.email !== "" && this.password !== ""){
                this.postClient();
                this.firstName = "";
                this.lastName = "";
                this.email = "";
                this.password = "";
            }
        },


        postClient: function(){
            axios.post('/api/clients',`firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(response => {
                    console.log('registered');
                })
                .catch(err => {
                    this.errorFoundSign = true;
                    console.error([err]);
                    let spanError = document.querySelector('.signin-error-message');
                    spanError.innerHTML = err.response.data;
                    
                    if(err.response.data.includes('Email already in use')){
                        this.email = "";
                        this.password = "";
                    }                    
                })
        },  

        postLoan: function(){
            this.noInterestRate = false;
            this.noMaxAmount = false;
            this.noName = false;
            this.noPayments = false;

            if(!this.loan.name){
                this.noName = true;
            }
            if(!this.loan.maxAmount){
                this.noMaxAmount = true;
            }
            if(!this.loan.payments.length){
                this.noPayments = true;
            }
            if(!this.loan.interestRate){
                this.noInterestRate = true;
            }
            if(this.loan.name && this.loan.maxAmount && this.loan.payments.length && this.loan.interestRate && this.loan.maxAmount >= 10000 && this.loan.interestRate >= 0 && this.loan.interestRate < 100 && this.loans.find(loan => loan.name.includes(this.loan.name))){
                axios.post('/api/genericLoans', {name: this.loan.name, maxAmount: this.loan.maxAmount, payments: this.loan.payments, interestRate: this.loan.interestRate})
                .then( response => {
                    Swal.fire({
                        customClass: 'modal-sweet-alert',
                        text: "Loan created!",
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
                        text: `The loan creation was not completed: ${err.message.includes('403')? err.response.data: "There was an error in the loan request."}`,
                    })
                })
            }
            
        },


        ///METODOS AUXILIARES///

        modifyClient: function(client){
            axios.patch(client._links.client.href, client)
                 .then( response => {this.loadData()})
                 .catch(err => console.error(err.message));
        },

        //LOGOUT
        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },



        // deleteClient: function(client){
        //     axios.delete(client._links.client.href)
        //          .then( response => {this.loadData()})
        //          .catch(err => console.error(err.message));
        // }

    },

}).mount("#app")