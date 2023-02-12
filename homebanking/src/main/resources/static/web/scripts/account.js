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
            promiseAccount = axios.get(`http://localhost:8080/api/accounts/${id}`);
            promiseClient = axios.get(`http://localhost:8080/api/clients/1`);
            Promise.all([promiseAccount, promiseClient]).then(response => {
                    this.account = {... response[0].data};
                    this.client = {... response[1].data};
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
            switch(this.dateFilter){
                case 10:       
                    let i = 0;
                    while(i < 6 && i < firstFilter.length){
                        this.filteredTransactions.push(firstFilter[i]);
                        i++;
                    }
                    break;
                
                case 30:
                    this.filteredTransactions = firstFilter.filter(transaction => new Date(transaction.date.slice(0,10)).getTime() > new Date().getTime() - 30 * 1000 * 60 * 60 * 24);
                    break;
                
                case 60:
                    this.filteredTransactions = firstFilter.filter(transaction => new Date(transaction.date.slice(0,10)).getTime() > new Date().getTime() - 60 * 1000 * 60 * 60 * 24);
                    break;

                case 90:
                    this.filteredTransactions = firstFilter.filter(transaction =>  new Date(transaction.date.slice(0,10)).getTime() > new Date().getTime() - 90 * 1000 * 60 * 60 * 24);
                    break;  
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

            this.visibleTransactions = transactionsArray[this.pageNumber - 1];
            this.totalPages = transactionsArray.length;
            
            
        },

        changePage: function(movement){
            this.pageNumber += movement;
            this.filterTransactions();
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


    }


}).mount("#app");
