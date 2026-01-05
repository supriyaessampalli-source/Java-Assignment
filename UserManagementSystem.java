import java.util.*;
enum Role {
    ADMIN,
    USER
}
class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) {
        super(message);
    }
}
class User {

    
    private static int counter = 1;


    private int id;
    private String name;
    private String email;
    private Role role;
    private boolean active;

    
    public User(String name, String email, Role role) {
        this.id = counter++;   
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = true;
    }

    // Getters (Encapsulation)
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return this.email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }


    @Override
    public String toString() {
        return "User [ID=" + id +
               ", Name=" + name +
               ", Email=" + email +
               ", Role=" + role +
               ", Active=" + active + "]";
    }
}
class UserService {

    private User[] users;
    private int count;
    
    public UserService(int size) {
        users = new User[size];
        count = 0;
    }

    public void addUser(User user) throws DuplicateUserException {
        if (count == users.length) {
            System.out.println("User storage is full!");
            return;
        }


        for (int i = 0; i < count; i++) {
            if (users[i].getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new DuplicateUserException(
                    "Duplicate email found: " + user.getEmail()
                );
            }
        }

        users[count++] = user;
        System.out.println("User added successfully!");
    }

    
    public void displayUsers() {
        if (count == 0) {
            System.out.println("No users available.");
            return;
        }

        for (int i = 0; i < count; i++) {
            System.out.println(users[i]);
        }
    }
}
public class UserManagementSystem {

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);

        UserService service = new UserService(5);

                while (true) {
            System.out.println("\n==== USER MANAGEMENT ====");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); 
            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter email: ");
                        String email = sc.nextLine();
                        System.out.print("Enter role (ADMIN/USER): ");
                        Role role = Role.valueOf(sc.nextLine().toUpperCase());
                        System.out.print("Is active (true/false): ");
                        
                        User user = new User(name, email, role);
                        service.addUser(user);
                    } catch (DuplicateUserException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid role entered!");
                    }
                    break;
                case 2:
                    service.displayUsers();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}

