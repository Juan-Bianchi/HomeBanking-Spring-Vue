const {createApp} = Vue;


// CODIGO PARA HACER XML REQUEST
// axios.get('http://localhost:8080/api/clients/current',{headers:{'accept':'application/xml'}})
//      .then(response => console.log(response.data))


createApp({
    data(){
        return{
            client: undefined,
            barOpen: true,
            cards: [],
            newCard: undefined,
            createdCombinations: [],
            type: "x",
            color: "Select",
            debitCards:[],
            creditCards:[],
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
            
        },

        loadData: function(){
            axios.get('/api/clients/current')
                 .then(response => {
                    this.client = {... response.data};
                    this.cards = [... this.client.cards.map(card => ({... card}))];
                    this.filterCards();
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
                        Swal.fire({
                            customClass: 'modal-sweet-alert',
                            text: "Card created!",
                            icon: 'success',
                            confirmButtonText: 'Accept'
                        }).then((result) => {
                            window.location.href = "http://localhost:8080/web/cards.html"; 
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


        logout(){
            this.client = undefined;
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },
    },


}).mount("#app")