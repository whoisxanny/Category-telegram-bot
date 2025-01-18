# Telegram Bot for Category Tree Management

A Telegram bot for managing a category tree using Spring Boot and PostgreSQL.  
The goal of the project is to create a bot that allows users to create, view, and delete category trees.

---

## Functionality

### Commands for Users
1. **/start**  
   Displays a welcome message and basic instructions.

2. **/help**  
   Lists all available commands with descriptions.

3. **/addElement `<parent>` `<child>`**  
   - Adds a root element if no parent is specified.  
   - Adds a child element to an existing parent.  
   - If the parent element does not exist, a corresponding message is displayed.

4. **/viewTree**  
   Displays the category tree in a structured text format.

5. **/removeElement `<name>`**  
   - Deletes the specified element.  
   - When a parent element is removed, all its child elements are also deleted.  
   - If the element does not exist, a corresponding message is displayed.

### Additional Commands (Optional Features)
1. **/download**  
   Downloads the category tree as an Excel file in a custom format.

2. **/upload**  
   Accepts an Excel file containing a category tree and saves all elements to the database.

---

## How to Launch the Application

1. Clone the project repository:
   ```bash
   git clone https://github.com/your-username/category-tree-bot.git
   cd category-tree-bot

### Configure the Database Connection

Update the `application.properties` file in the `src/main/resources` directory with your PostgreSQL credentials:

`spring.datasource.url=jdbc:postgresql://localhost:5432/your-database`  
`spring.datasource.username=your-username`  
`spring.datasource.password=your-password`

### Install Dependencies and Build the Project

Run the following command to install dependencies and build the project:  
`mvn clean install`

### Run the Application

Start the application using the following command:  
`mvn spring-boot:run`

### Start Your Telegram Bot

Obtain a bot token from BotFather on Telegram. Update the bot token in your `application.properties` file:  
`telegram.bot.token=your-bot-token`  
Restart the application and interact with your bot on Telegram.

### Technologies Used

**Java 17**, **Spring Boot**, **Spring Data JPA**, **PostgreSQL**, **TelegramBots Library**, **Apache POI** (for Excel file processing), **Maven**, **Lombok**

### Notes

The bot uses the **Command** design pattern for handling commands. The project follows **SOLID** principles. All interactions with the database are managed using **Spring Data JPA**.

### Project Team

**Allan Allanazarov**, Developer  


