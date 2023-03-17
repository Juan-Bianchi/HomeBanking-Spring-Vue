const {createApp} = Vue;

createApp({

    components: { Datepicker: VueDatePicker },
    
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
            dateFilter: 5,
            navNumbersArray: [],
            visibleNavNumbers: [],
            emptyLinesAmount: undefined,
            currentNavModulus: 0,
            date: undefined,
            filterByDate : "true",
            format: undefined,
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
            let client = axios.get(`http://localhost:8080/api/clients/current`)
            let accounts = axios.get('http://localhost:8080/api/clients/current/activeAccounts')
            Promise.all([client, accounts]).then(response => {
                    this.client = {... response[0].data};
                    this.account = response[1].data.find(account => account.id == id);  
                    const date = Vue.ref();
                    if(this.date){
                        date.value = [this.date[0], this.date[1]];
                    }
                    this.format = (date) => {
                        if(this.date){
                            const day1 = this.date[0].getDate();
                            const month1 = this.date[0].getMonth() + 1;
                            const year1 = this.date[0].getFullYear();
    
                            const day2 = this.date[1].getDate();
                            const month2 = this.date[1].getMonth() + 1;
                            const year2 = this.date[1].getFullYear();
    
                            return `${day1}/${month1}/${year1} - ${day2}/${month2}/${year2}`
                        }
                        
                    }
                    this.loadTransactions();
                    
                })
                //.catch(err => console.error(err.message));
        },

        loadTransactions: function(){
            if(this.filterByDate.includes("true") && this.date){
                
                axios.get(`http://localhost:8080/api/transactions?accountNumber=${this.account.number}&fromDate=${this.date[0].toISOString()}&thruDate=${this.date[1].toISOString()}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response =>{
                         this.transactions = response.data.map(transaction => transaction);
                         this.manageData();
                    })
                    .catch(err => console.error(err.message))
            }
            else {
                console.log("object");
                axios.get(`http://localhost:8080/api/transactions?accountNumber=${this.account.number}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response =>{
                         this.transactions = response.data.map(transaction => transaction);
                         this.manageData();
                    })
                    .catch(err => console.error(err.message))
            }
            
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
        },


        filterTransactions: function(){
            let firstFilter = this.orderedTransactions.filter(transaction => this.activeChecks.some(category => category.startsWith(transaction.type)));
            this.filteredTransactions = [];
            this.dateFilter = Number.parseInt(this.dateFilter);
            // if(this.filterByDate.includes("true")){
            //     if(this.date){
            //         this.filteredTransactions = firstFilter.filter(transaction => (new Date(transaction.date.slice(0,10)).getTime() >=  this.date[0].getTime()) &&
            //         (new Date(transaction.date.slice(0,10)).getTime() <=  this.date[1].getTime()));
            //     }
            //     else{
            //         this.date = [];
            //         this.date.push(new Date(new Date().setDate(new Date().getDate()-7)));
            //         this.date.push(new Date(new Date().setDate(this.date[0].getDate() + 7)));
            //         const date = Vue.ref();
            //         date.value = [this.date[0], this.date[1]];
            //         this.filteredTransactions = [];
            //     }
            // }
            // else{
            //     let i = 0;
            //     while(i < this.dateFilter && i < firstFilter.length){
            //         this.filteredTransactions.push(firstFilter[i]);
            //         i++;
            //     };
            // }
            if(this.filterByDate.includes("false")){

                console.log(this.dateFilter);
                let i = 0;
                while(i < this.dateFilter && i < firstFilter.length){
                    this.filteredTransactions.push(firstFilter[i]);
                    i++;
                };
            }
            else{
                this.filteredTransactions = firstFilter;
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
            if(transactionsArray.length){
                this.totalPages = transactionsArray.length;
            }
           
            if(this.totalPages === 1){
                this.pageNumber = 1;
            }
            this.visibleTransactions = transactionsArray[this.pageNumber - 1];  
              
            
            let numbers = [];
            for(let i = 1; i <= this.totalPages; i++){
                numbers.push(i);
            }
            counter = 0;
            this.navNumbersArray = [];
            while(counter < this.totalPages){
                this.navNumbersArray.push( numbers.slice(counter, counter+=3) );
            }
            this.visibleNavNumbers = this.navNumbersArray[this.currentNavModulus];
            if(!this.visibleTransactions){
                this.visibleTransactions = [];
            }     
            this.emptyLinesAmount = 10 - this.visibleTransactions.length;
        },

        changePage: function(movement){
            this.pageNumber += movement;
            this.currentNavModulus = Math.floor((this.pageNumber - 1) / 3);
            this.renderTransactions();
        },

        goToPage: function(page){
            this.pageNumber = page;
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


    },


}).mount("#app");
