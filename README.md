API:



Entities:

* User:
  Long id
  String name
  String email
  String password
  String avatarUrl
  Availability availability

* Availability:
  User user
  LocalTime: unavailableFrom
  LocalTime: unavailableTo
  TemporaryAvailability availability

* Enum TemporaryAvailability
  AVAILABLE, UNAVAILABLE, TEMPORARY

//TODO date(?? creation, game time planned)
* Lobby:
  Long id
  String title  
  LocalTime date
  User owner


* Invite:
  Long id
  String message
  Lobby forLobby
  Set<UserAccept> users
  InviteState state

* UserAccept:
  User user
  boolean accept


* Enum InviteState:
  PENDING, DECLINED, ACCEPTED, DELAYED

    
 