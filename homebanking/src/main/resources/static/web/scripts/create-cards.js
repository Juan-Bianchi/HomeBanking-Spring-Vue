const {createApp} = Vue;


createApp({
    data(){
        return{
            client: undefined,
            barOpen: true,
            cards: [],
            newCard: undefined,
            createdCombinations: [],
            type: "x",
            color: "x",
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
                    this.manageData();
                    
                 })
        },

        loadNewCard: function(){
            this.newCard = {
                type : undefined,
                color : undefined,
                cardHolder : this.client.firstName + " " + this.client.lastName,
                number : "XXXX-XXXX-XXXX-XXXX",
                fromDate : (new Date().getMonth() + 1) + "/" + new Date().getFullYear(),
                thruDate : (new Date().getMonth() + 1) + "/" + (new Date().getFullYear() + 5),
            }
            console.log(this.newCard);
        },

        createFilterRadioLists: function(){
            this.createdCombinations = this.cards.map(card =>{
                return {
                    type: card.type,
                    color: card.color,
                }
            } );
            console.log(this.createdCombinations);
            
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