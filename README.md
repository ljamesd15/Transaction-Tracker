# Transactions-Tracker

	This program, using SQLite, stores information about each user, their transactions, and 
program preferences. With a text user interface a user can log into an account (and log out), 
check their current balance, add new transactions, look at their transaction history (soon), and 
edit program preferences (soon). Users can use Transactions Tracker to monitor their expenses and 
see how much they are spending in certain categories, at certain locations, and much much more.
Transactions Tracker allows users to become more fiscally responsible and aware of where money is
both coming in from and more importantly where it is going to.

PATCH NOTES

v 1.3.05 Edited all method headers and comments to ensure clarity for both future implementors and
	clients. (12/26/17)

v 1.3.04 Changed program menu options to only log in if no user is logged in and to only offer
	to log someone out if someone is logged out. (12/25/2017)

v 1.3.03 Log in, create new user, log out, and add new expense all work properly. Added an
	account balance for users which updates whenever a transaction is added. (12/24/17)

v 1.3.02 Added the ability to create new users, log in, log out. Fixed some bugs (12/22/17)

v 1.3.01 Rewrote the application to make use of SQLite database instead of storing info on a 
	txt or csv file. (12/22/2017)

v 1.2.23 Added enums to Transaction, implemented view, organized methods to controller package. 
	Output files will now be written in CSV format (10/26/17)

v 1.2.22 Moved files to their respective MVC positions. Began rewriting the view code so that
	it will optimize the transaction building process along with looking a lot cleaner. (10/24/17)

v 1.2.21 Changed the way a file is created. Added a toString and comparable to
 	 Transaction. Attempted to add a sorting feature for transactions still 
 	 needs debugging. (6/3/2017)

v 1.2.20 Streamlined the ability to add both deposits and withdrawals so both
     transactions' information are stored in the same way and in the future
     if need be. (11/24/2016)

v 1.2.10 Fixed the auto-complete date if the purchase was today bug. Corrected
     the expense price to only accept positive numbers. Added the ability
     keep track of deposits as well. (11/4/2016)

v 1.2.00 Changed the way the expense data is stored by creating actual expense 
     objects. Combined the helper classes as inner classes within the 
     expense object class. (10/30/2016)

v 1.1.50 The information will now be printed to a file based on its month of
     purchase. If the file for that month of purchase does not exist, then
     one will be created and the information will be printed to it. 
     (10/20/2016)

v 1.1.41 Added a quit option to the opening. Edited getDate method and method 
     names to make more sense. (10/16/2016)

v 1.1.40 Separated files to create a main class which can do more than just
     create the expense objects and will be expanded upon later. 
     (10/15/2016)

v 1.1.30  Added categories option to further describe the expense created. 
      Fixed a few string bugs. Added the capability to edit the information
      multiple times in a row before finalizing and allowing the user to
      see the most updated version of the purchase information. Changed the
      way the purchase information is stored. Created a new class file for
      all supporting methods. (10/14/2016)

v 1.1.21  Made the program match with boolean zen. Made method comments more
      readable. Added new responses to edit information and made it easier
      to move faster through it. Added another slot in the purchase 
      information storage for the price so it will no longer need to return
      price everywhere as I replaced all of it with a parse from a string 
      to double. The final statement of total price was redone. Allowed
      program to deal with invalid responses in a similar way.
      (10/12/2016)

v 1.1.20  Cleaned up code a bit. Re-did method head comments and in-line
      as well. Increased the overall readability of the program. Added an
      escape sequence as well if the user decided they didn't want to edit
      the purchase information. Improved the yesNo method. Made certain
      methods private. (10/02/2016)

v 1.1.10  The user is now able to edit the information if there are any errors 
      before they are printed to the file. In addition invalid response 
      messages were added along with exception flags which are thrown when 
      there are information type mismatches. (10/01/2016)

v 1.1.00  The user is now allowed to add a date corresponding to the expense in
      order to better keep track of the expenses. (09/20/16)
