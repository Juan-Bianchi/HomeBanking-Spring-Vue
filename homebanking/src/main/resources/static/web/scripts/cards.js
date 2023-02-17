const {createApp} = Vue;

createApp({
    
    data(){
        return {
            client: undefined,
            cards: [],
            creditCards: [],
            debitCards: [],
            windowWidth: screen.width,
        }
    },

    created(){
        this.loadData();
    },

    mounted(){
        window.addEventListener('resize', this.onResize);
    },

    methods:{

        loadData: function(){
            axios.get(`http://localhost:8080/api/clients/1`)
                 .then(response => {
                    this.client = {... response.data};
                    this.cards = this.client.cards.map(card => ({...card}));
                    this.manageData();
                 })
        },

        manageData(){
            this.filterCards();
        },

        filterCards: function(){
            let credit = this.cards.filter(card => card.type.includes('CREDIT'));
            this.creditCards = credit.sort((c1, c2) => c2.color > c1.color? -1: 1);
            let debit = this.cards.filter(card => card.type.includes('DEBIT'));
            this.debitCards = debit.sort((c1, c2) => c2.color > c1.color? -1: 1);
        },

        splitNumber: function(number){
            const reg = /-/g;
            return number.replace(reg, ' ');
        },

        getCardDate: function(date){
            let arrayDate = date.split('-');
            return arrayDate[1] + "/" + arrayDate[0];
        },


        // WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width;
        },

    },

}).mount('#app')