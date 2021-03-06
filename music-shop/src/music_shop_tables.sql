Drop table LeadSinger;
Drop table HasSong;
Drop table ReturnItem;
Drop table Return;
Drop table PurchaseItem;
Drop table Purchase;
Drop table Customer;
Drop table Item;

drop sequence receiptID_counter;
drop sequence retid_counter;

create table Item(
	upc int not null,
	title varchar(50) not null,
	type  varchar(10) not null,
	category varchar(20) not null,
	company varchar(40) not null,
	year varchar(4) not null,
	price float not null,
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
	cid varchar(10) not null ,
	cpassword varchar(10) not null,
	cname varchar(40) not null,
	caddress varchar(60) not null,
	cphone varchar(20) not null,
	PRIMARY KEY (cid)
	);
	
create sequence receiptID_counter
start with 1
increment by 1;
	
create table Purchase(
	receiptID int not null,
	Pdate date not null,
	cid  varchar(10),
	cardN varchar(16) ,
	expiryDate varchar(10) ,
	expectedDate date ,
	deliveredDate date,
	PRIMARY KEY (receiptID),
	FOREIGN KEY (cid) references Customer
	);
	

create table PurchaseItem(
	receiptID int not null ,
	upc int not null ,
	quantity int not null,
	PRIMARY KEY (receiptID, upc),
	FOREIGN KEY (upc) references Item,
	FOREIGN KEY (receiptID) references Purchase
	);
	
create sequence retid_counter
start with 1
increment by 1;
	
create table Return(
	retid int not null,
	redate date not null,
	receiptID int not null,
	PRIMARY KEY (retid),
	FOREIGN KEY (receiptID) references Purchase
	);
	
	
create table ReturnItem(
	retid int not null ,
	upc int not null ,
	quantity int not null,
	PRIMARY KEY (retid, upc),
	FOREIGN KEY (upc) references Item,
	FOREIGN KEY (retID) references Return
	);

insert into Item
values(11111, 'The Key', 'CD', 'pop', 'BMG', '2013', 12.50, 100);
insert into LeadSinger
values(11111, 'Eason Chen');		
insert into HasSong
values(11111, 'Farewell Saha');		
insert into HasSong
values(11111, 'The Wanderer');	
insert into HasSong
values(11111, 'So far So close');		


insert into Item
values(11112, 'In A World Like This', 'CD', 'pop', 'BMG', '2013', 15.99, 200);
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
values(11113, 'Everything Has Changed', 'CD', 'pop', 'BMG', '2013', 10.65, 200);
insert into LeadSinger
values(11113, 'Taylor Swift');	
insert into LeadSinger
values(11113, 'Ed Sheeran');		
insert into HasSong
values(11113, 'Everything Has Changed');		

insert into Item
values(11114, 'Tunnel Vision', 'DVD', 'RandB', 'RCA Records', '2013', 10.99, 100);
insert into LeadSinger
values(11114, 'Justin Timberlake');			
insert into HasSong
values(11114, 'Tunnel Vision');	

insert into Item
values(11115, 'Mr. Timberlake', 'DVD', 'RandB', 'RCA Records', '2008', 10.35, 100);
insert into LeadSinger
values(11115, 'Justin Timberlake');		
insert into LeadSinger
values(11115, 'Beyonce');		
insert into HasSong
values(11115, 'Until the End of Time');

insert into Item
values(11116, 'Pacific Rim', 'DVD', 'classic', 'Sony', '2013', 10.25, 100);
insert into LeadSinger
values(11116, 'Ramin Djawadi');			
insert into HasSong
values(11116, 'Pacific Rim');	
insert into HasSong
values(11116, 'Gipsy Danger');	
insert into HasSong
values(11116, 'Just a Memory');	
insert into HasSong
values(11116, 'Better than New');	

insert into Customer
values('Andypw', '1234', 'Andy Lau', '1234 Main Mall', '6048221111');
insert into Customer
values('Hebepw', '1234', 'Hebe T', '1221 Main Mall', '6048220000');
insert into Customer
values('Tonypw', '1234', 'Tony Leung', '1200 Main Mall', '6048222222');
insert into Customer
values('Jonepw', '1234', 'Jone Ross', '1200 Main Mall', '6048223333');
insert into Customer
values('Rachelpw', '1234', 'Rachel Green', '1204 Main Mall', '6048224444');
insert into Customer
values('cpw', '1234', 'Chandler Bing', '1205 Main Mall', '6048225555');

insert into Purchase
values(receiptID_counter.nextval, '2013-08-01', null, '1234123412341234', '2014-01', null, null);	
insert into PurchaseItem
values(receiptID_counter.currval, 11111, 1);
	
insert into Purchase
values(receiptID_counter.nextval, '2013-08-02', null, null, null, null, null);	
insert into PurchaseItem
values(receiptID_counter.currval, 11111, 1);	
insert into PurchaseItem
values(receiptID_counter.currval, 11112, 1);	

insert into Purchase
values(receiptID_counter.nextval, '2013-07-02', 'Rachelpw', '1234123412341111', '2014-03', '2013-07-06',  '2013-07-05');	
insert into PurchaseItem
values(receiptID_counter.currval, 11112, 2);		
insert into PurchaseItem
values(receiptID_counter.currval, 11115, 1);	
insert into PurchaseItem
values(receiptID_counter.currval, 11113, 1);

insert into Purchase
values(receiptID_counter.nextval, '2013-08-01', null, '1234123412341222', '2015-01', null, null);	
insert into PurchaseItem
values(receiptID_counter.currval, 11111, 1);	
insert into PurchaseItem
values(receiptID_counter.currval, 11112, 1);	

insert into Purchase
values(receiptID_counter.nextval, '2013-08-02', null, null, null, null, null);	
insert into PurchaseItem
values(receiptID_counter.currval, 11111, 1);	
insert into PurchaseItem
values(receiptID_counter.currval, 11112, 1);

insert into Purchase
values(receiptID_counter.nextval, '2013-08-02', null, null, null, null, null);	
insert into PurchaseItem
values(receiptID_counter.currval, 11111, 1);	
insert into PurchaseItem
values(receiptID_counter.currval, 11115, 1);

insert into Purchase
values(receiptID_counter.nextval, '2013-08-02', null, null, null, null, null);	
insert into PurchaseItem
values(receiptID_counter.currval,  11111, 1);	
insert into PurchaseItem
values(receiptID_counter.currval,  11112, 1);
insert into PurchaseItem
values(receiptID_counter.currval,  11113, 1);	
insert into PurchaseItem
values(receiptID_counter.currval,  11115, 1);

	
insert into Return
values(retid_counter.nextval, '2013-07-22', 2);
insert into ReturnItem
values(retid_counter.currval, 11111, 1);	

insert into Return
values(retid_counter.nextval, '2013-08-02', 3);
insert into ReturnItem
values(retid_counter.currval, 11112, 1);

insert into Return
values(retid_counter.nextval,'2013-08-05', 4);
insert into ReturnItem
values(retid_counter.currval, 11111, 1);
