
# 📝 Banking CLI Application – Planning Document

## 1. 📌 Project Overview

**Project Name**:  
_Banking CLI Application_

**Objective**:  
_Build a command-line interface application to simulate basic banking operations like account management, deposits, withdrawals, and transaction tracking._

---

## 2. 📋 Project Scope

**Included Features**:
- [ ] Create Checking/Savings Accounts
- [ ] View Balance
- [ ] Deposit & Withdraw Funds
- [ ] Transfer Funds Between Accounts
- [ ] View Transaction History

**Excluded (Future Consideration)**:
- [ ] GUI
- [ ] Mobile App Integration
- [ ] External Payment Gateways
- [ ] Multi-user Support

---

## 3. 🧑‍🤝‍🧑 Stakeholders

| Stakeholder           | Role                         | Expectations                          |
|-----------------------|------------------------------|----------------------------------------|
| Developer (You)       | Builder of the application   | Easy to maintain, test, and extend     |
| End User              | Bank employee or customer    | Fast, reliable, easy-to-use CLI        |



---

## 4. ✅ Functional Requirements

| ID  | Requirement Description                                      |
|-----|--------------------------------------------------------------|
| FR1 | User can create a new account (checking/savings)            |
| FR2 | User can deposit money into an account                      |
| FR3 | User can withdraw money from an account                     |
| FR4 | User can transfer money between accounts                    |
| FR5 | User can view balance of an account                         |
| FR6 | User can view transaction history for an account            |
| FR7 | Application validates sufficient balance before withdrawal  |
| FR8 | User can check rates given by the bank updated every call   |

---

## 5. ⚙️ Non-Functional Requirements

| Category      | Requirement                                      |
|---------------|--------------------------------------------------|
| Usability     | Commands must be intuitive and include help text |
| Performance   | All operations should complete in <1s            |
| Security      | CLI must prevent unauthorized actions            |
| Portability   | Should work on Windows, Linux, and macOS         |
| Maintainability | Code should be modular and documented         |

---

## 6. 🧪 Use Cases 

### Use Case: Create Account
- **Actor**: User
- **Trigger**: User enters `create-account [AccountType] [Deposit]`
- **Steps**:
  1. Prompt for name, account type (checking/savings), and initial deposit
  2. Validate input
  3. Store account details in the system
  4. Confirm creation with account number

### Use Case: Deposit Funds
- **Actor**: User
- **Trigger**: User enters `deposit [Reciever AccountNumber] [Amount]`
- **Steps**:
  1. Prompt for account number and deposit amount
  2. Validate account and amount
  3. Update balance
  4. Show confirmation


### Use Case: View Account 
- **Actor**: User 
- **Trigger**: User enters `view-account [AccountNumber]`
- **Steps**:
  1. Prompt for account number and account pin 
  2. Validate account details 
  3. Show confirmation 

### Use Case: Check Rate 
- **Actor**: User 
- **Trigger**: User enters `rate [AccountNumber] [AccountPin]`
- **Steps**: 
  1. Prompt for account number 
  2. Validate account details 
  3. Show confirmation 

### Use Case: Transfer Funds 
- **Actor**: User 
- **Trigger**: User enters `transfer [SenderAccountNumber] [RecieverAccountNumber] [Amount]`
- **Steps**
  1. Prompt for account number 
  2. Validate account details 
  3. Show confirmation 


---

## 7. 🉑  Acceptance Criteria 

### Use Case: Create Account 
- Prompt for name , account type (checking / savings ) , and initial deposit
- Validate Input 
- Store account details in correct system 
- Confirm creation with account number 

### Use Case: Deposit Funds 
- Prompt for account number and deposit amount 
- Validate account and amount 
- Update Balance 
- Show confirmation

### Use Case: View Account 
- Open endpoint for viewing account when attaching accountNumber and other details 
- Validate account details 
- Show Confirmation



### Use Case: Check Rate
- Endpoint for viewing  most update version of rate when attaching account number and other details 
- Validate Account 
- Update rate with bank of england endpoint 
- Show confirmation 

### Use Case: Transfer Funds
- Transfer Endpoint with account receiver and sender account number -deposit info 
- Details Validation 
- Transfer Information should be persistent stored in a database 
- Confirmation of Transfer 




---

## 8. ⚖️ Feasibility Analysis

| Type            | Feasibility Assessment                             |
|------------------|---------------------------------------------------|
| Technical        | Yes – CLI tools and local databases are manageable |
| Economic         | No cost for personal project                      |
| Time             | Estimated 2-3 weeks for MVP                       |
| Legal/Compliance | Not needed unless used in real banking context    |

---

## 9. 🧰 Technology Stack

| Layer              | Choice              |
|--------------------|---------------------|
| Programming Language | Java (Spring Boot ) |
| Database            | PostgreSQL          |
| CLI Framework       | JCommander (Java)   |
| Version Control     | Git                 |
| Testing Framework   | JUnit               |

---

## 10. ⚠️ Risk Assessment

| Risk                          | Likelihood | Mitigation                              |
|-------------------------------|------------|------------------------------------------|
| Data loss                     | Medium     | Regular backups / persistence layer      |
| Unauthorized access           | Medium     | Add basic authentication if needed       |
| Poor usability                | Low        | Focus on intuitive CLI design            |
| Scope creep                   | High       | Lock features for MVP                    |

---

## 11. 📚 Documentation Plan

You should keep the following documents up to date:
- Feature list (with status)
- CLI Command Reference (how to use each command)
- Design diagrams (optional, for data flow or architecture)
- User manual (for end users)
- Dev guide (for future developers)


## 12. 🧠  Design Considerations 

| **Feature**         | **CheckingAccount** | **SavingAccount** |
|---------------------|---------------------|-----------------|
| Supports overdraft  | Yes    ✅            | No         ❌     |
| Monthly interest    | No        ❌         | Yes        ✅    |
 | Minimum balance enforced | Optional            | Yes (Usually) ✅    | 
|   Monthly maintenace fees | Common    ✅         | Rare  ❌          | 