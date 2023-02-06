const {createApp} = Vue;

createApp({
    data(){
        return{
            account: undefined,
            transactions: undefined,
            windowWidth: window.innerWidth,
            barOpen: true,
        }
    },

    created(){
        this.loadData();
    },

    mounted() {
        window.addEventListener('resize', this.onResize);
    },

    methods: {
        loadData: function(){
            const urlString = location.search;
            const parameters = new URLSearchParams(urlString);
            const id = parameters.get('id');
            axios.get(`http://localhost:8080/api/accounts/${id}`)
                 .then(response => {
                    this.account = {... response.data};
                    console.log(this.account);
                    this.transactions = [...this.account.transactions].map(transaction => transaction);
                    
                })
                 //.catch(err => console.error(err.message));
        },

        onResize(event) {
            this.windowWidth = screen.width
        },

        toggleMenu: function(){
            if(this.barOpen){
                document.getElementById("sidebar").style.left = "-100vw";
                document.querySelector(".small-menu-transaction").style.top = "0";
                this.barOpen = false;
            }
            else {
                document.getElementById("sidebar").style.left = "0";
                document.querySelector(".small-menu-transaction").style.top = "-100vh";
                this.barOpen = true;
            }
            
        },
    }


}).mount("#app");
