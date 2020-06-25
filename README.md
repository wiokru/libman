#LibMan
Library Manager - helps with reservations and borrowing books.

## 1.General
In system there are three main user roles
1. User
2. Librarian
3. Administrator

Each role has the same permissions as role above them - so, librarian is also regular user and admin is also librarian and regular user.

**User**

User can search thorough books and select some of them for reservation.  
User can reserve max.3 books. If he wants more he should borrow the ones that are reserved already.  
User also can view his borrowed and reserved books - tab "My books".  
User can edit his information, for example change phone number, city, etc.

**Librarian**

Librarian can do the same things as user, but moreover he has "Manage Books" tab.
It allows him to add new book, add new author, add new book category, mark books as borrowed (accept reservations), reject reservations and mark books as returned.
He also can edit and delete existing books.

**Administrator**

Administrator has the same privilages as librarian and user. He also has ability to delete and edit exising users. For example he can add new roles to users.

