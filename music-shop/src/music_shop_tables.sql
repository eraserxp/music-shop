Drop table LeadSinger;
Drop table HasSong;
Drop table ReturnItem;
Drop table Return;
Drop table PurchaseItem;
Drop table Purchase;
Drop table Customer;
Drop table Item;


create table Item(
	upc int not null,
	title varchar(50) not null,
	type  varchar(10) not null,
	category varchar(20) not null,
	company varchar(40) not null,
	year char(10) not null,
	price int not null,
	stock int not null,
	PRIMARY KEY (upc)
	);


create table LeadSinger(
	upc int not null ,
	name varchar(40) not null,
	PRIMARY KEY (upc, name),
	FOREIGN KEY (upc) references Item
	);


create table HasSong(
	upc int not null,
	title varchar(50) not null,
	PRIMARY KEY (upc, title),
	FOREIGN KEY (upc) references Item
	);


create table Customer(
	cid varchar(8) not null ,
	cpassword varchar(10) not null,
	cname varchar(40) not null,
	caddress varchar(60) not null,
	cphone int not null,
	PRIMARY KEY (cid)
	);
	
	
create table Purchase(
	receiptID varchar(10) not null,
	Pdate varchar(20) not null,
	cid  varchar(8) null,
	cardN int ,
	expiryDate varchar(20) ,
	expectedDate varchar(20) ,
	deliveredDate varchar(20),
	PRIMARY KEY (receiptID),
	FOREIGN KEY (cid) references Customer
	);
	

create table PurchaseItem(
	receiptID varchar(10) not null ,
	upc int not null ,
	quantity int not null,
	PRIMARY KEY (receiptID, upc),
	FOREIGN KEY (upc) references Item,
	FOREIGN KEY (receiptID) references Purchase
	);
	
	
create table Return(
	retid varchar(10) not null,
	redate varchar(20) not null,
	receiptID varchar(10) not null,
	PRIMARY KEY (retid),
	FOREIGN KEY (receiptID) references Purchase
	);
	
	
create table ReturnItem(
	retid varchar(10) not null ,
	upc int not null ,
	quantity int not null,
	PRIMARY KEY (retid, upc),
	FOREIGN KEY (upc) references Item,
	FOREIGN KEY (retID) references Return
	);

insert into Item
values(11111, 'The Key', 'CD', 'pop', 'EMI', '2013', 12, 100);
insert into LeadSinger
values(11111, 'Eason Chen');		
insert into HasSong
values(11111, 'Farewell Saha');		
insert into HasSong
values(11111, 'The Wanderer');	
insert into HasSong
values(11111, 'So far So close');		


insert into Item
values(11112, 'In A World Like This', 'CD', 'pop', 'BMG', '2013', 15, 200);
insert into LeadSinger
values(11112, 'Backstrret Boys');		
insert into HasSong
values(11112, 'In A World Like This');		
insert into HasSong
values(11112, 'Breathe');	
insert into HasSong
values(11112, 'Trust Me');	
insert into HasSong
values(11112, 'Try');	
insert into HasSong
values(11112, 'Make Believe');	

insert into Item
values(11113, 'Everything Has Changed', 'CD', 'pop', 'Big Machine Records', '2013', 10, 200);
insert into LeadSinger
values(11113, 'Taylor Swift');	
insert into LeadSinger
values(11113, 'Ed Sheeran');		
insert into HasSong
values(11113, 'Everything Has Changed');		

insert into Item
values(11114, 'Tunnel Vision', 'DVD', 'RandB', 'RCA Records', '2013', 10, 100);
insert into LeadSinger
values(11114, 'Justin Timberlake');			
insert into HasSong
values(11114, 'Tunnel Vision');	

insert into Item
values(11115, 'Mr. Timberlake', 'DVD', 'RandB', 'Jive', '2008', 10, 100);
insert into LeadSinger
values(11115, 'Justin Timberlake');		
insert into LeadSinger
values(11115, 'Beyonce');		
insert into HasSong
values(11115, 'Until the End of Time');

insert into Customer
values('Andy', 'Andypw', 'Andy Lau', '1234 Main Mall', 6048221111)
insert into Customer
values('Hebe', 'Hebepw', 'Hebe T', '1221 Main Mall', 6048220000)
insert into Customer
values('Tony', 'Tonypw', 'Tony Leung', '1200 Main Mall', 6048222222)

insert into Purchase
values('A123456789', '2013-08-01', null, 1234123412341234, '2014-01-01', null, null);	
insert into PurchaseItem
values('A123456789', 11111, 1);	

insert into Purchase
values('A111111111', '2013-08-02', null, null, null, null, null);	
insert into PurchaseItem
values('A111111111', 11111, 1);	

insert into Purchase
values('A111111112', '2013-07-02', 'Tony', 1234123412341111, '2014-03-03', '2013-07-06', '2013-07-05');	
insert into PurchaseItem
values('A111111112', 11112, 2);	
