const {createApp} = Vue;



createApp( {
    data(){
        return{
            client: undefined,
            windowWidth: window.innerWidth,
            accounts: [],

        }
    },

    created() {
        this.loadData();
    },

    mounted() {
        window.addEventListener('resize', this.onResize);
    },

    methods: {
        loadData(){
            axios.get("http://localhost:8080/api/clients/1")
                 .then(response => {
                    this.client = {... response.data};
                    this.accounts = this.client.accounts.map(account => account);
                 })
                 .catch(err => console.error(err.message));
        },

        onResize(event) {
            this.windowWidth = screen.width
        },



    },

}).mount("#app");