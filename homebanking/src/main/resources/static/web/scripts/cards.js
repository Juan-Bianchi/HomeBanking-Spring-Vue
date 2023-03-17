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
            let client = axios.get(`http://localhost:8080/api/clients/current`)
            let cards = axios.get('http://localhost:8080/api/clients/current/activeCards')
            Promise.all([client, cards])
                    .then(response => {
                            this.client = {... response[0].data};
                            console.log(response[1].data);
                            this.cards = response[1].data.map(card => ({... card,
                                         isExpired: new Date().getTime() > new Date(card.thruDate).getTime(), 
                                         isAboutToExpire: new Date(card.thruDate).getTime() - new Date().getTime() < 30 * 3600 * 24 * 1000 &&
                                                          new Date(card.thruDate).getTime() - new Date().getTime() > 0,
                                        })
                            );                                     
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

        canAddCreditCard: function(){
            let canAdd = false;
            let allowedCards = this.creditCards.filter(card => card.isAboutToExpire || card.isExpired);
            if(this.creditCards.length - allowedCards.length < 3){
                canAdd = true;
            }

            return canAdd;
        },

        canAddDebitCard: function(){
            let canAdd = false;
            let allowedCards = this.debitCards.filter(card => card.isAboutToExpire || card.isExpired);
            if(this.debitCards.length - allowedCards.length < 3){
                canAdd = true;
            }

            return canAdd;
        },

        cancelCard: function(card){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the card cancellation',
                text: `If you accept the card number ${card.number} will be cancelled. You will not be able to make future purchases with the card, and it will not be available in your homebanking anymore. If you want to cancel the request, just click 'Close' button.`,
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                if (result.isConfirmed) {
                    card.isActive = false;
                    axios.patch('/api/clients/current/cards',`cardNumber=${card.number}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response => {
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Card cancelled",
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
                           text: err.message,
                       })
                    })
                }
              })
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