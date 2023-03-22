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
            dinamicText: "",
            messages: [" nurture your wealth...", " fulfill your dreams...", " become our client! "],
            current: 0,
            adder: 0,
        }
    },

    created(){
        this.loadCurrentClient();
    },

    mounted(){
        window.addEventListener('scroll', this.scrollFunction);
        this.manageAutoTyping(); 
        this.controlCarousel();
    },

    methods: {

        //LOGIN

        login: function(){
            axios.post('/api/login',`email=${this.emailLog}&password=${this.passwordLog}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                 .then(response => {
                    console.log('signed in!!!');

                    axios.get('/api/clients/current')
                         .then(response => {
                             this.client = {... response.data};
                             console.log(this.client);
                             if(!this.client.newLogin){
                                this.client.newLogin = new Date().toLocaleString();
                                console.log(this.client.newLogin);
                            }
                            axios.post(`/api/clients/current/lastLogin`,`email=${this.emailLog}&newloginDate=${new Date().toLocaleString()}&lastLoginDate=${this.client.newLogin}`)
                                .then(response => window.location.href = "/web/accounts.html")
                                .catch(err => console.log([err]));
                         })
                         .catch(err => console.error(err.message));
                    
                    
                })
                 .catch(err => {
                    console.error(err.message);
                    this.errorFound = true;
                });
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
                    window.location.href = "/web/index.html";
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

        controlCarousel: function(){
            const myCarousel = document.getElementById('carousel1');
            const directionButtons = [... document.querySelectorAll('.btn-carousel')];

            myCarousel.addEventListener('slide.bs.carousel', event => {
                directionButtons[0].style.opacity = '0';
                directionButtons[1].style.opacity = '0';
            })

            myCarousel.addEventListener('slid.bs.carousel', event => {
                directionButtons[0].style.opacity = '1';
                directionButtons[1].style.opacity = '1';
            })
        },

        //autotyping

        async manageAutoTyping(){

            let i = 0;
            let current = 0;
            while(true) {
                current = await this.typeSentence(current);
                await this.waitForMs(1500);
                current = await this.deleteSentence(current);
                await this.waitForMs(1500);
                i++
                if(i >= this.messages.length) {i = 0;}
            }
        },

        async typeSentence(current) { 

            let i = 0;
            while(i < this.messages[current].length) {
                await this.waitForMs(100);
                this.dinamicText += this.messages[current].charAt(i);
                i++
            }
            current ++;
            return current;                
        },

        async deleteSentence(current){

            while(this.dinamicText.length > 0) {
                await this.waitForMs(100);
                this.dinamicText = this.dinamicText.slice(0,-1);
            }
            if(current > 2){
                current = 0;
            }
            
            return current;
        },

        waitForMs(ms) {
            return new Promise(resolve => setTimeout(resolve, ms))
        },


    },

}).mount("#app")
