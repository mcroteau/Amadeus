<style>
    #credit-card{
        background:#f8f8f8;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        border-radius: 4px;
    }
</style>

<h1>Go Live</h1>

<div id="start-ad-container">
    <div id="credit-card"></div>
    <div id="processing"></div>
</div>

<form action="/o/flyer/start" method="post" id="start-ad-form">
    <input type="hidden" name="id" value="${flyer.id}"/>
    <input type="hidden" name="stripeToken" id="stripe-token" value=""/>
</form>

<button id="start">Go Live!</button>

<script src="https://js.stripe.com/v3/"></script>
<script type="text/javascript">

    var stripe = {},
        elements = {},
        card = {};

    var goLiveContainer = document.querySelector("#start-ad-container")

    var startAdButton = document.querySelector("#start"),
        startAdForm = document.querySelector("#start-ad-form"),
        creditCard = document.querySelector("#credit-card"),
        processingGoLive = document.querySelector("#processing"),
        stripeToken = document.querySelector("#stripe-token");

    stripe = Stripe("pk_test_KYVCbdaOAuezlE7sF7cn2hnK");
    elements = stripe.elements()

    card = elements.create('card', {
        base : {
            fontSize: '29px',
            lineHeight: '48px'
        }
    })
    card.mount('#credit-card')
    card.addEventListener('change', function(event) {
        var displayError = document.getElementById('card-errors');
        if (event.error) {
            processing.innerHTML = event.error.message
            processing.style.display = "block"
        } else {
            processing.style.display = "none"
            processing.innerHTML = "Processing... "
        }
    });

    startAdButton.addEventListener("click", function(event){
        event.preventDefault()
        processing.style.display = "block"
        stripe.createToken(card).then(function(result) {
            if(result.token.hasOwnProperty('id')) {
                stripeToken.value = result.token.id
                startAdForm.submit()
            }else{
                processing.innerHTML = "Please notify us, nothing was charged... "
            }
        });
    })

</script>
