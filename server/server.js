var express = require('express');
var app = express();
var fs = require('fs');

// file is included here:
var Ticket=require('./ticket.js');
var User=require('./user.js');
//eval(fs.readFileSync('ticket.js')+'');
//eval(fs.readFileSync('user.js'));

app.configure(function(){
  app.use(express.bodyParser());
  app.use(express.cookieParser('CMOVProj1'));
  app.use(express.session({'secret':'CMOVProj1'}));
  app.use(app.router);
});

app.get('/', function(req, res){
  res.send('hello world2');
    next();
});

app.post('/auth_user',function(req,res){
	res.set('Content-Type','application/json');
	if(req.get('Content-Type')!="application/json"){
		res.status(400).send(JSON.stringify({'error':'needs to be JSON'}));
        return;
	}
	if(req.body.username=="teste" && req.body.password=="1234"){
		req.session['UserId']=0;
        res.status(200).send(JSON.stringify({'sessionId':'abcdefgh'}));
        return;
	}
    req.session=null;

	res.status(401).send(JSON.stringify('invalid login'));

});


app.post('/buy_ticket',function(req,res){
	res.set('Content-Type','application/json');
	if(req.get('Content-Type')!="application/json"){
		res.status(400).send(JSON.stringify({'error':'needs to be JSON'}));
		return;
	}
    if(req.session['UserId']!=null){
        res.send(JSON.stringify(new Ticket()));
        return;
    }

	res.status(401).send(JSON.stringify('invalid login'));
});

app.listen(3000);
