var express = require('express');
var app = express();


app.configure(function(){
  app.use(express.bodyParser());
  app.use(app.router);
  app.use(express.cookieSession());
});

app.get('/', function(req, res){
  res.send('hello world2');
});

app.post('/auth_user',function(req,res){
	res.set('Content-Type','application/json');
	if(req.get('Content-Type')!="application/json"){
		res.status(400).send(JSON.stringify({'error':'needs to be JSON'}));
		return;
	}
	if(req.body.username=="teste" && req.body.password=="1234"){
		res.status(200).send(JSON.stringify({'sessionId':'abcdefgh'}));
	}

	res.status(401).send(JSON.stringify('invalid login'));
});


app.post('/buy_ticket',function(req,res){
	res.set('Content-Type','application/json');
	if(req.get('Content-Type')!="application/json"){
		res.status(400).send(JSON.stringify({'error':'needs to be JSON'}));
		return;
	}


	res.status(401).send(JSON.stringify('invalid login'));
});

app.listen(3000);
