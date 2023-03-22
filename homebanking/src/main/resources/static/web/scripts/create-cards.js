const {createApp} = Vue;


// CODIGO PARA HACER XML REQUEST
// axios.get('http://localhost:8080/api/clients/current',{headers:{'accept':'application/xml'}})
//      .then(response => console.log(response.data))


createApp({
    data(){
        return{
            client: undefined,
            orderedAccounts: [],
            account: undefined,
            barOpen: true,
            cards: [],
            newCard: undefined,
            createdCombinations: [],
            type: "x",
            color: "Select",
            debitCards:[],
            creditCards:[],
            localStorage: [],
            notifCounter: 0,
        }
    },

    created(){
        this.loadData();
    },

    mounted() {
        window.addEventListener('resize', this.onResize);
    },


    methods:{

        manageData: function(){
            this.loadNewCard();
            this.createFilterRadioLists();
            const urlString = location.search;
            const parameters = new URLSearchParams(urlString);
            this.type = parameters.get('type');
            this.filterCards();
        },

        loadData: function(){
            let client = axios.get(`/api/clients/current`)
            let cards = axios.get('/api/clients/current/activeCards')
            let accounts = axios.get('/api/clients/current/activeAccounts')
            Promise.all([client, cards, accounts])
                    .then(response => {
                            this.client = {... response[0].data};
                            console.log(response[1].data);
                            this.orderedAccounts = response[2].data.sort((a1, a2) => { return a1.id > a2.id ? 1: -1; });
                            this.cards = response[1].data.map(card => ({... card, 
                                                                            anotherAllowed: new Date(card.thruDate).getTime() - new Date().getTime() < 30 * 3600 * 24 * 1000
                                                                        })
                            );
                            console.log(this.cards); 
                            let data = localStorage.getItem('notif');
                            if(data){
                                this.localStorage = JSON.parse(localStorage.getItem('notif'));
                            }
                            this.notifCounter = this.localStorage.filter(element => element.isRead == false).length;                                    
                            this.manageData();
                    })
        },

        loadNewCard: function(){
            this.newCard = {
                type : this.type,
                color : this.color,
                cardHolder : this.client.firstName + " " + this.client.lastName,
                number : "XXXX-XXXX-XXXX-XXXX",
                fromDate : (new Date().getMonth() + 1) + "/" + new Date().getFullYear(),
                thruDate : (new Date().getMonth() + 1) + "/" + (new Date().getFullYear() + 5),
            }
        },

        createFilterRadioLists: function(){
            this.createdCombinations = this.cards.map(card =>{
                return {
                    type: card.type,
                    color: card.color,
                    anoterAllowed: card.anotherAllowed,
                }
            } );       
        },

        createCard: function(){
            Swal.fire({
                customClass: 'modal-sweet-alert',
                title: 'Please confirm the card creation',
                text: "If you accept the card will be created. If you want to cancel the request, just click 'Close' button.",
                icon: 'warning',
                showCancelButton: true,          
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/clients/current/cards',`cardType=${this.type}&cardColor=${this.color}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response => {
                        this.localStorage.push({
                            number: response.data.number,
                            description: `A ${response.data.type.slice(0,1) + response.data.type.slice(1).toLowerCase()}card with number ${response.data.number} has been created.`,
                            isRead: false,
                            isDeleted: false,
                        })
                        localStorage.removeItem('notif');
                        localStorage.setItem('notif', JSON.stringify(this.localStorage));
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Card created!",
                            icon: 'success',
                            confirmButtonText: 'Accept'
                        }).then((result) => {
                            window.location.href = "/web/cards.html"; 
                        })
                    })
                    .catch(err =>{
                       console.log([err])
           
                       Swal.fire({
                           customClass: 'modal-sweet-alert',
                           icon: 'error',
                           title: 'Oops...',
                           text: err.message.includes('403')? err.response.data: err.message,
                       })
                    })
                }
              })
        },

        filterCards: function(){
            let credit = this.cards.filter(card => card.type.includes('CREDIT'));
            this.creditCards = credit.sort((c1, c2) => c2.color > c1.color? -1: 1);
            let debit = this.cards.filter(card => card.type.includes('DEBIT'));
            this.debitCards = debit.sort((c1, c2) => c2.color > c1.color? -1: 1);
        },


        //METHODS USED WHEN MOUNTED

        onResize(event) {
            this.windowWidth = screen.width
        },

        chooseAccount: function(destinantion){
            let template ="";
            this.orderedAccounts.forEach(account => {
             template +=`<input class="form-check-input me-2" type="radio" name="account" id="${account.number}" value="${account.number}">
                 <label class="form-check-label me-4" for=${account.number}>
                    ${account.number}
                </label>`;
                
            });
            Swal.fire({
                customClass: 'modal-sweet-alert',
                icon: 'warning',
                title: 'Please select an account.',
                html:
                    '<div>' +
                        template +
                    '</div>',

                showCloseButton: true,
                showCancelButton: true,
                cancelButtonColor: '#d33',
                cancelButtonText: 'Close',
                confirmButtonText: 'Accept'
            }).then((result) => {
                    account = [ ...document.querySelectorAll('.swal2-container input[name="account"]')].find(element => element.checked);
                    this.account = account.value;
                    if (result.isConfirmed) {
                        if(destinantion.includes('transfer')){
                            window.location.href = `/web/transfers.html?number=${this.account}`
                        }
                        else{
                            window.location.href = `/web/account.html?id=${this.orderedAccounts.find(account => account.number.includes(this.account)).id}`
                        }
                    }
                })
        },

        toggleMenu: function(){

            let sidebar = document.getElementById("sidebar");
            let smallMenu = document.querySelector(".small-menu-transaction");
            let contentWrapper = document.querySelector(".transaction-wrapper");
            
            if(this.barOpen){
                sidebar.style.left = "-100vw";
                smallMenu.style.top = "4vh";
                contentWrapper.style.width = `${this.windowWidth - 280}px`;
                this.barOpen = false;
            }
            else {
                sidebar.style.left = "2vw";
                smallMenu.style.top = "-1000vh";
                contentWrapper.style.width = `${this.windowWidth - 280}px`;
                this.barOpen = true;
            }
            
        },

        toggleChevron(id){
            let button = document.getElementById(`toggleChevron${id}`);
            (button.style.transform === "") ? button.style.transform = "rotateX(180deg)": button.style.transform = "";
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


        logout(){
            this.client = undefined;
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "/web/index.html";
            })
        },
    },


}).mount("#app")