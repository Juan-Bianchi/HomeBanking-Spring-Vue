const {createApp} = Vue;

createApp({
    data() {
        return{
            url: "",
            firstName: "",
            lastName: "",
            email:"",
            clientsJSON: undefined,
            clients : [],
            clientChange:{},
            
        }

    
    },

    created() {
        this.url = "http://localhost:8080/rest/clients";
        this.loadData();
    },


    methods: {
        loadData: function(){  
            axios.get(this.url)
                 .then(response => {
                    this.clientsJSON = response.data;
                    this.clients = response.data._embedded.clients.map(client => ({... client}));

                 })
                 .catch(err => console.error(err.message));
        },

        addClient() {
           
            if( this.lastName !== "" && this.firstName !== "" && this.email !== "" ){
                this.postClient();
                this.firstName = "";
                this.lastName = "";
                this.email = "";

            }
        },


        postClient: function(){
            axios.post(this.url, {
                firstName: this.firstName,
                lastName: this.lastName,
                email: this.email,
                })
                 .then(response => this.loadData())
                 .catch(err => console.error(err.message));
        },  


        ///METODOS AUXILIARES///

        modifyClient: function(client){
            axios.patch(client._links.client.href, client)
                 .then( response => {this.loadData()})
                 .catch(err => console.error(err.message));
        },


        deleteClient: function(client){
            axios.delete(client._links.client.href)
                 .then( response => {this.loadData()})
                 .catch(err => console.error(err.message));
        }

    },

}).mount("#app")