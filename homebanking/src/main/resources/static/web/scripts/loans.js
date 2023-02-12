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
            axios.get(`http://localhost:8080/api/clients/1`)
                .then(response => {
                    this.client = {... response.data};
                    this.loans = this.client.loans.map(loan => ({... loan})).sort((l1, l2) => (l1.id > l2.id ? 1: -1));;
                    this.manageData();
                    console.log(this.loans);
                })
                //.catch(err => console.error(err.message));
        },

        manageData: function(){

        },

        onResize(event) {
            this.windowWidth = screen.width
        },

    }
}).mount("#app")