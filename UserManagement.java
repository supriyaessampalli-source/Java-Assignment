import java.util.*;

enum UserRole {
    ADMIN,
    USER
}

class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) {
        super(message);
    }
}

class User {
    private static int idCounter = 1;
    private int id;
    private String name;
    private String email;
    private UserRole role;
    private boolean active;

    public User(String name, String email, UserRole role, boolean active) {
        this.id = idCounter++;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return active; }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return email.equalsIgnoreCase(other.email);
    }

    public int hashCode() {
        return email.toLowerCase().hashCode();
    }

    public String toString() {
        return "User { id = " + id + ", name = " + name + ", email = " + email + ", role = " + role + ", active = " + active + " }";
    }
}

class UserService {

    private Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
    private Map<String, List<String>> loginHistory = Collections.synchronizedMap(new HashMap<>());
    public void addUser(User user) throws DuplicateUserException {
        if (users.containsKey(user.getEmail())) {
            throw new DuplicateUserException(
                    "Duplicate email not allowed: " + user.getEmail()
            );
        }
        users.put(user.getEmail(), user);
    }

    public List<User> getUsersByRole(UserRole role) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<User> getActiveUsersSortedByName() {
        List<User> activeUsers = new ArrayList<>();

        Iterator<User> iterator = users.values().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }

        activeUsers.sort(
                Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER)
        );

        return Collections.unmodifiableList(activeUsers);
    }

    public void recordLogin(String email) {
        loginHistory
                .computeIfAbsent(email, e -> new ArrayList<>())
                .add("User logged in");
    }

    public Map<String, List<String>> getLoginHistory() {
        return Collections.unmodifiableMap(loginHistory);
    }

    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }
}

public class UserManagement {

    public static void main(String[] args) {

        UserService service = new UserService();

        try {
            service.addUser(new User("supriya", "supriya@gmail.com", UserRole.ADMIN, true));
            service.addUser(new User("bhanu", "bhanu@gmail.com", UserRole.USER, true));
            service.addUser(new User("ravi", "ravi@gmail.com", UserRole.USER, false));

        } catch (DuplicateUserException e) {
            System.out.println("Error: " + e.getMessage());
        }

        service.recordLogin("supriya@gmail.com");
        service.recordLogin("bhanu@gmail.com");
        service.recordLogin("ravi@gmail.com");

        System.out.println("\n--- ALL USERS ---");
        for (User user : service.getAllUsers()) {
            System.out.println(user);
        }

        System.out.println("\n--- ADMIN USERS ---");
        service.getUsersByRole(UserRole.ADMIN)
                .forEach(System.out::println);

        System.out.println("\n--- ACTIVE USERS SORTED BY NAME ---");
        service.getActiveUsersSortedByName()
                .forEach(System.out::println);

        System.out.println("\n--- LOGIN HISTORY ---");
        service.getLoginHistory().forEach((email, history) ->
                System.out.println(email + " -> " + history)
        );
    }
}