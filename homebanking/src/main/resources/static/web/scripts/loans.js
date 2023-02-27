const {createApp} = Vue;


createApp({
    data(){
        return{
            client: undefined,
            loans: [],
            windowWidth: window.innerWidth,
        }
    },

    created(){
        this.loadData();
    },

    mounted(){
        window.addEventListener('resize', this.onResize);
    },

    methods: {
        loadData: function(){
            axios.get(`http://localhost:8080/api/clients/current`)
                .then(response => {
                    this.client = {... response.data};
                    this.loans = this.client.loans.map(loan => ({... loan})).sort((l1, l2) => (l1.id > l2.id ? 1: -1));;
                    this.manageData();
                })
                .catch(err => console.error(err.message));
        },

        manageData: function(){

        },

        onResize(event) {
            this.windowWidth = screen.width
        },

        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },

    }
}).mount("#app")