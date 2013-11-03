
-- Table: User
CREATE TABLE User ( 
    id       INTEGER PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT
                     NOT NULL
                     UNIQUE,
    username VARCHAR NOT NULL
                     UNIQUE,
    password VARCHAR NOT NULL,
    devID    VARCHAR NOT NULL
                     UNIQUE,
    token    VARCHAR UNIQUE 
);


-- Table: CreditCard
CREATE TABLE CreditCard ( 
    id     INTEGER        PRIMARY KEY AUTOINCREMENT,
    number NUMERIC( 16 )  NOT NULL
                          UNIQUE,
    csc    NUMERIC( 3 ),
    userid INTEGER        REFERENCES User ( id ) 
);


-- Table: Ticket
CREATE TABLE Ticket ( 
    id         INTEGER       PRIMARY KEY AUTOINCREMENT,
    ticketCode VARCHAR       NOT NULL,
    userid     INTEGER       REFERENCES User ( id ),
    type       INTEGER( 1 )  NOT NULL,
    buyDate    DATETIME      NOT NULL,
    useDate    DATETIME      DEFAULT ( NULL ) 
);


-- Table: validator
CREATE TABLE validator ( 
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    [key]      VARCHAR NOT NULL
                       UNIQUE,
    devID      VARCHAR NOT NULL
                       UNIQUE,
    sessionKey VARCHAR UNIQUE 
);


-- Table: Conductor
CREATE TABLE Conductor ( 
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER REFERENCES User ( id ) 
);

