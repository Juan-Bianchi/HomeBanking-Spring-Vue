const {createApp} = Vue;


createApp({
    data(){
        return{
            client: undefined,
            loans: [],
            genericLoans: [],
            windowWidth: window.innerWidth,
            activeChecks: ["Mortgage", "Personal", "Automotive"],
            filteredLoans: [],
            typeOfLoans: [],
            totalPages: undefined,
            pageNumber: undefined,
            visibleLoans: undefined,
            navNumbersArray: [],
            currentNavModulus: 0,
            numbersModulus: undefined,
            visibleNavNumbers: [],
            emptyLinesAmount: undefined,
            availableAmounts: [],
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
            let ownLoans = axios.get('http://localhost:8080/api/clients/current');
            let genericLoans = axios.get('http://localhost:8080/api/loans');
            Promise.all([ownLoans, genericLoans])
                   .then(response => {    
                        this.client = {... response[0].data};
                        this.loans = this.client.loans.map(loan => ({... loan})).sort((l1, l2) => (l1.id > l2.id ? 1: -1));
                        console.log(this.loans);
                        this.genericLoans = [... response[1].data].sort((l1, l2)=> (l1.id - l2.id));
                        this.manageData();
                   })
                   //.catch(err => console.error(err.message));
        },

        manageData: function(){
            this.filterLoans();
            this.createTypeOfLoansList();
            this.showAvailableAmounts();
        },

        createTypeOfLoansList: function(){
            this.typesOfLoans = [... new Set(this.genericLoans.map(loan => loan.name))];
        },

        filterLoans: function(){
            this.filteredLoans = this.loans.filter(loan => this.activeChecks.some(category => category.startsWith(loan.name)));
            this.renderLoans();
        },

        showAvailableAmounts: function(){
            let mortgage = this.loans.filter(loan => loan.name.toLowerCase().includes('mortgage')).reduce(((total, loan)=> total + loan.amount),0);
            let personal = this.loans.filter(loan => loan.name.toLowerCase().includes('personal')).reduce(((total, loan)=> total + loan.amount),0);
            let automotive = this.loans.filter(loan => loan.name.toLowerCase().includes('automotive')).reduce(((total, loan)=> total + loan.amount),0);

            console.log(mortgage, personal, automotive);
            this.availableAmounts.push(({
                name: 'Mortgage',
                available: this.genericLoans.find(loan=> loan.name.includes('Mortgage')).maxAmount - mortgage,
            }))
            this.availableAmounts.push(({
                name: 'Personal',
                available: this.genericLoans.find(loan=> loan.name.includes('Personal')).maxAmount - personal,
            }))
            this.availableAmounts.push(({
                name: 'Automotive',
                available: this.genericLoans.find(loan=> loan.name.includes('Automotive')).maxAmount - automotive,
            }))
        },


        renderLoans: function(){

            let size = this.filteredLoans.length;
            let counter = 0;
            let transactionsArray = [];

            while(counter < size){
                transactionsArray.push(this.filteredLoans.slice(counter, counter+=5));
            }

            this.totalPages = transactionsArray.length;
            if(this.totalPages === 1){
                this.pageNumber = 1;
            }
            this.visibleLoans = transactionsArray[this.pageNumber - 1];
            
            let numbers = [];
            for(let i = 1; i <= this.totalPages; i++){
                numbers.push(i);
            }
            counter = 0;
            while(counter < this.totalPages){
                this.navNumbersArray.push( numbers.slice(counter, counter+=3) );
            }
            this.visibleNavNumbers = this.navNumbersArray[this.currentNavModulus];
            this.emptyLinesAmount = 5 - this.visibleLoans.length;
        },
        
        changePage: function(movement){
            this.pageNumber += movement;
            this.currentNavModulus = (this.pageNumber - 1) / 3;
            this.renderLoans();
        },

        goToPage: function(page){
            this.pageNumber = page;
            this.renderLoans();
        },

        onResize(event) {
            this.windowWidth = screen.width
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
}).mount("#app")