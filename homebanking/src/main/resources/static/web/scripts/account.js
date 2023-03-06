const {createApp} = Vue;

createApp({
    data(){
        return{
            account: undefined,
            client: undefined,
            transactions: undefined,
            windowWidth: window.innerWidth,
            barOpen: true,
            orderedTransactions: [],
            typesOfTransaction: [],
            filteredTransactions: [],
            visibleTransactions: [],
            totalPages: 1,
            pageNumber: 1,
            activeChecks:['DEBIT', 'CREDIT'],
            dateFilter: 10,
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
            axios.get(`http://localhost:8080/api/clients/current`).then(response => {
                    this.client = {... response.data};
                    this.account = this.client.accounts.find(account => account.id == id);  
                    console.log(this.account)       
                    this.transactions = [...this.account.transactions].map(transaction => transaction);
                    this.manageData();
                })
                .catch(err => console.error(err.message));
        },


        manageData: function(){
            this.orderTransactions();
            this.createTypeOfTransactionList();
            this.filterTransactions();
        },


        orderTransactions: function(){
            this.orderedTransactions = this.transactions.map(transaction => ({... transaction}));
            this.orderedTransactions.sort((t1, t2) => (t1.date > t2.date ? -1: 1));
        },


        createTypeOfTransactionList: function(){
            this.typesOfTransaction = [... new Set(this.orderedTransactions.map(transaction => transaction.type))];
            console.log(this.typesOfTransaction);
        },


        filterTransactions: function(){
            let firstFilter = this.orderedTransactions.filter(transaction => this.activeChecks.some(category => category.startsWith(transaction.type)));
            this.filteredTransactions = [];
            this.dateFilter = Number.parseInt(this.dateFilter);
            if(this.dateFilter === 10){
                let i = 0;
                while(i < 6 && i < firstFilter.length){
                    this.filteredTransactions.push(firstFilter[i]);
                    i++;
                }
            }
            else{
                this.filteredTransactions = firstFilter.filter(transaction => new Date(transaction.date.slice(0,10)).getTime() > new Date().getTime() - this.dateFilter * 1000 * 60 * 60 * 24);
            }
            this.renderTransactions();
        },


        renderTransactions: function(){

            let size = this.filteredTransactions.length;
            let counter = 0;
            let transactionsArray = [];

            while(counter < size){
                transactionsArray.push(this.filteredTransactions.slice(counter, counter+=10));
            }

            this.totalPages = transactionsArray.length;
            if(this.totalPages === 1){
                this.pageNumber = 1;
            }
            this.visibleTransactions = transactionsArray[this.pageNumber - 1];      
        },
        

        changePage: function(movement){
            this.pageNumber += movement;
            this.renderTransactions();
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
            axios.post('/api/logout')
                 .then(response => {
                    console.log('signed out!!!');
                    localStorage.removeItem('currentClient');
                    window.location.href = "http://localhost:8080/web/index.html";
            })
        },


    }


}).mount("#app");
