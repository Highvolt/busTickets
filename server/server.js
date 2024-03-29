var express = require('express');
var app = express();
var fs = require('fs');
var db=require('./busTicketdb.js').init('db.db');

// file is included here:
var Ticket=require('./ticket.js');
var User=require('./user.js');
//eval(fs.readFileSync('ticket.js')+'');
//eval(fs.readFileSync('user.js'));
Ticket.setDB(db);
User.setDB(db);
app.configure(function(){
  app.use(express.bodyParser());
  app.use(app.router);
  app.use(function(err, req, res, next) {
    console.log(err);
    res.send(500);
  });

});
User.generateIVandKEY();

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

app.post('/getPrice',User.verifyKey,function(req,res,next){
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
},Ticket.calculatePrice);




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


app.post("/myTickets",User.verifyKey,Ticket.getAllValidTickets);

app.post("/verifyTicket",User.verifyKey,Ticket.iSvalidateTicket);
app.post("/getTicketsBus",User.verifyKey,Ticket.getAllValitedTicketsForBus);


app.post('/register',
    function(req,res,next){
        if(req.body.username==null || req.body.password==null || req.body.device==null || req.body.number==null || req.body.csc==null || req.body.expire==null ){
            res.status(400).send(JSON.stringify({'msg':'Missing attributes','username':req.body.username==null,'password':req.body.password==null,'device':req.body.device==null,
            'number':req.body.number==null,'csc':req.body.csc==null,'expire':req.body.expire==null}));
        }else{
            User.register(req.body.username,req.body.password,req.body.device,req.body.number,req.body.csc,req.body.expire,next);
        }
    },
    function(data,req,res,next){
        //console.log(JSON.stringify(req));
        if(data==-1){
                res.status(500).send('');
            }else if(data==-2){
                res.status(409).send(JSON.stringify({'msg':'Username taken'}));
            }else if(data.errno){
                res.status(500).send(JSON.stringify(data));
            }else{
            //TODO gen token
                res.send(JSON.stringify({'token':data}));
            }
        //next();
    }
);

app.post('/validateTicket',User.validKeyBus,Ticket.validate);
app.post('/login',
     function(req,res,next){
        if(req.body.username==null || req.body.password==null || req.body.device==null ){
            res.status(400).send(JSON.stringify({'msg':'Missing attributes','username':req.body.username==null,'password':req.body.password==null,'device':req.body.device==null}));
        }else{
            User.login(req.body.username,req.body.password,req.body.device,next);
        }
    },
    function(data,req,res,next){
        if(data==-1){
                res.status(500).send('');
        }else if(data==-2){
                res.status(400).send(JSON.stringify({'msg':'Bad Auth information'}));
        }else if(data.errno!=null){
            res.status('500').send(JSON.stringify(data));
        }else{
                res.status(200).send(JSON.stringify({'token':data}));
        }

    }
);

app.post('/loginBust',
     function(req,res,next){
        if(req.body.password==null || req.body.device==null ){
            res.status(400).send(JSON.stringify({'msg':'Missing attributes'}));
        }else{
            User.busLogin(req.body.password,req.body.device,next);
        }
    },
    function(data,req,res,next){
        if(data==-1){
                res.status(500).send('');
        }else if(data==-2){
                res.status(400).send(JSON.stringify({'msg':'Bad Auth information'}));
        }else if(data.errno!=null){
            res.status('500').send(JSON.stringify(data));
        }else{
                res.status(200).send(JSON.stringify(data));
        }

    }
);

app.listen(3000);
