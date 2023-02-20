const {createApp} = Vue;

createApp({
    data(){
        return{
            client: undefined,
            id: undefined,
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            emailLog: undefined,
            passwordLog: undefined,
            errorFound: false,
            errorFoundSign : false,
            missingFields: false,
            foundEmail: false,
            registered: false,
        }
    },

    created(){

    },

    methods: {

        login: function(){
            axios.post('/api/login',`email=${this.emailLog}&password=${this.passwordLog}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                 .then(response => {
                    console.log('signed in!!!');
                    this.errorFound = false;
                    window.location.href = "http://localhost:8080/web/accounts.html"
                })
                 .catch(err => {
                    console.error(err.message);
                    this.errorFound = true;
                });
            this.emailLog = undefined
            this.passwordLog = undefined
        },


        loginWithEnter: function(){
            if(this.email && this.password){
                this.login();
            }
        },

        signIn: function(){
            axios.post('/api/clients',`firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(response => {
                    console.log('registered');
                    this.errorFoundSign = false;
                    this.firstName = "";
                    this.lastName = "";
                    this.email = "";
                    this.password = "";
                    this.registered = true;
                })
                .catch(err => {
                    this.errorFoundSign = true;
                    console.error([err]);
                    let spanError = document.querySelector('.signin-error-message');
                    console.log(err.response.data);
                    if(err.response.data.includes('Missing')){
                        spanError.innerHTML = "⋆There are missing fields in the form, fill every field and submit."
                    }
                    if(err.response.data.includes('Name')){
                        spanError.innerHTML = "⋆The email used to sign in is already in use."
                        this.email = "";
                        this.password = "";
                    }                    
                })
            
        },

        resetBooleans: function(){
            this.errorFound = false;
            this.errorFoundSign = false;
            this.firstName = "";
            this.lastName = "";
            this.email = "";
            this.password = "";
            this.emailLog = undefined;
            this.passwordLog = undefined;  
            this.registered = false;
        },

        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },
    },

}).mount("#app")
