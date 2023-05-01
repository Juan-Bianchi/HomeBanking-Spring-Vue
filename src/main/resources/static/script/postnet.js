const {createApp} = Vue;

createApp({
    data(){
        return{
            cardNumber: "1234 5678 9123 4567",
            description: "",
            amount: undefined,
            cvv : undefined,
        }
    },

    created(){

    },

    methods: {

        cleanInput: function(){
            this.cardNumber = "";
        },

        patternMatch: function() {
            if(this.cardNumber.length == 4 || this.cardNumber.length == 9 || this.cardNumber.length == 14){
                this.cardNumber += "-";
            }
        },

        completeTransaction: function() {
            console.log(this.cardNumber);
            axios.post('http://localhost:8080/api/cards/transactions', { number: this.cardNumber, cvv: this.cvv, amount: this.amount, description: this.description})
                .then(response => console.log("completed"))
                .catch(err => console.error([err]));
        }
    },

}).mount("#app")


    