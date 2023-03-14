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
            registered: false,
            current: 0,
            adder: 0,
        }
    },

    created(){
        this.loadCurrentClient();
    },

    mounted(){
        window.addEventListener('scroll', this.scrollFunction);
    },

    methods: {

        //LOGIN

        login: function(){
            axios.post('/api/login',`email=${this.emailLog}&password=${this.passwordLog}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                 .then(response => {
                    console.log('signed in!!!');
                    console.log([response])
                    this.errorFound = false;
                    this.emailLog = undefined;
                    this.passwordLog = undefined;
                    window.location.href = "http://localhost:8080/web/accounts.html"
                })
                 .catch(err => {
                    console.error(err.message);
                    this.errorFound = true;
                });
            this.emailLog = undefined;
            this.passwordLog = undefined;
        },


        loginWithEnter: function(){
            if(this.emailLog && this.passwordLog){
                this.login();
            }
        },

        signIn: function(){
            axios.post('/api/clients',`firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(response => {
                    console.log('registered');

                    this.emailLog = this.email;
                    this.passwordLog = this.password;

                    this.errorFoundSign = false;
                    this.firstName = "";
                    this.lastName = "";
                    this.email = "";
                    this.password = "";
                    this.registered = true;

                    this.login();
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
            this.client = undefined;
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },


        //WHEN CREATED

        loadCurrentClient: function(){
            axios.get('/api/clients/current')
                 .then(response => {
                    this.client = {... response.data};
                    console.log(this.client);
                 })
                 .catch(err => console.error(err.message));
        },


        // WHEN MOUNTED

        //change bar heigth

        scrollFunction() {

            let icon = document.querySelector(".logo-landing-page");

            if (document.body.scrollTop > 80 || document.documentElement.scrollTop > 80) {
                if(screen.width >= 768){
                    icon.style.height  = "55px";
                    icon.style.width = "auto";
                }
                
            } else {
                icon.style.height = "70px";
                icon.style.width = "auto";
            }
        },


    },

}).mount("#appVue")
