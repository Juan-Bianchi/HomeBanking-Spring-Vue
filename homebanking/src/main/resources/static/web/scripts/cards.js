const {createApp} = Vue;

createApp({
    
    data(){
        return {
            client: undefined,
            cards: [],
            creditCards: [],
            debitCards: [],
            windowWidth: screen.width,
            showInfoCards : true,
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
            axios.get(`http://localhost:8080/api/clients/current`)
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

        hideCardNumber: function(number){
            let splittedNumber = number.split('-');
            for(let i=0; i < 3; i++){
                splittedNumber[i] = '••••';
            }
            return splittedNumber.join(' ');
        },

        getCardDate: function(date){
            let arrayDate = date.split('-');
            return arrayDate[1] + "/" + arrayDate[0];
        },
        

        // WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width;
        },

        logout(){
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },

    },

}).mount('#app')