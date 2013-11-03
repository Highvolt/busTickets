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
  app.use(app.router);
  app.use(function(err, req, res, next) {
    console.log(err);
    res.send(500);
  });

});

app.get('/', function(req, res){
  res.send('CMOV Assignment 1');
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


app.post('/buy_ticket',User.verifyKey,function(req,res,next){
	res.set('Content-Type','application/json');
	if(req.get('Content-Type')!="application/json"){
		res.status(400).send(JSON.stringify({'error':'needs to be JSON'}));
		return;
	}
    console.log(JSON.stringify(req.body));
    if(req.body.key!=null && (req.body.t1!=null || req.body.t2!=0 || req.body.t3!=null)){
        //res.send(JSON.stringify(new Ticket()));
        //res.send(JSON.stringify(Ticket.createAndSign(req.body.key,req.body.type)));
        next();
        return;
    }

	res.status(400).send(JSON.stringify('invalid login'));
},Ticket.processRequest);

app.listen(3000);
