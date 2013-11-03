module.exports=BusTicket;
var sqlite3= require('sqlite3').verbose();
var fs=require('fs');

function BusTicket(){
}

BusTicket.init=function(file){
  var exists = fs.existsSync(file);

	if( !exists )
	{
		console.log("Creating DB file: " + file );
		fs.openSync(file, "w");
	}

	var db = new sqlite3.Database(file);

	if( !exists )
	{
		// Força que todas as queries dentro do serialize, e ao mesmo nível,
		// sejam executadas em série e não em paralelo.

		db.serialize(function(){

			db
				.run("BEGIN;")

				// Cria as tabelas

				.run("CREATE TABLE User ("+
                        "id       INTEGER PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT NOT NULL UNIQUE,"+
                        "username VARCHAR NOT NULL UNIQUE,"+
                        "password VARCHAR NOT NULL,"+
                        "devID    VARCHAR NOT NULL UNIQUE,"+
                        "token    VARCHAR UNIQUE"+
                        ");")

				.run("CREATE TABLE CreditCard ( "+
                        "id     INTEGER        PRIMARY KEY AUTOINCREMENT,"+
                        "number NUMERIC( 16 )  NOT NULL UNIQUE,"+
                        "csc    NUMERIC( 3 ),"+
                        "userid INTEGER        REFERENCES User ( id ) "+
                        ");")

				.run("CREATE TABLE Ticket (" +
                        "id         INTEGER       PRIMARY KEY AUTOINCREMENT,"+
                        "ticketCode VARCHAR       NOT NULL,"+
                        "userid     INTEGER       REFERENCES User ( id ),"+
                        "type       INTEGER( 1 )  NOT NULL,"+
                        "buyDate    DATETIME      NOT NULL,"+
                        "useDate    DATETIME      DEFAULT ( NULL )"+
                        ");")

				.run("CREATE TABLE validator ("+
                        "id         INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "[key]      VARCHAR NOT NULL UNIQUE,"+
                        "devID      VARCHAR NOT NULL UNIQUE,"+
                        "sessionKey VARCHAR UNIQUE"+
                        ");")

				.run("CREATE TABLE Conductor ("+
                        "id     INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "userId INTEGER REFERENCES User ( id )"+
                        ");"
                    )

				// Insere os dados na db

				.run("COMMIT;");
		});
	}
    return db;
}
