-- =========================================
-- EXTENSION
-- =========================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================
-- TABLE: book
-- =========================================
CREATE TABLE book (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn VARCHAR(50) UNIQUE NOT NULL,
    total_copies INT NOT NULL CHECK (total_copies >= 0),
    available_copies INT NOT NULL CHECK (available_copies >= 0),
    CONSTRAINT chk_book_copies CHECK (available_copies <= total_copies)
);

-- =========================================
-- TABLE: member
-- =========================================
CREATE TABLE member (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    max_active_loans INT NOT NULL,
    allow_member_to_borrow_when_overdue_loan BOOLEAN NOT NULL DEFAULT FALSE,
    loan_due_days INT NOT NULL
);

-- =========================================
-- TABLE: loan
-- =========================================
CREATE TABLE loan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id UUID NOT NULL,
    member_id UUID NOT NULL,
    borrowed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    returned_at TIMESTAMP NULL,

    CONSTRAINT fk_loan_book
        FOREIGN KEY (book_id)
        REFERENCES book(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_loan_member
        FOREIGN KEY (member_id)
        REFERENCES member(id)
        ON DELETE CASCADE
);

-- =========================================
-- TABLE: configuration
-- =========================================
CREATE TABLE configuration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    max_active_loans INT NOT NULL,
    allow_member_to_borrow_when_overdue_loan BOOLEAN NOT NULL DEFAULT FALSE,
    loan_due_days INT NOT NULL
);

-- =========================================
-- INDEXES
-- =========================================
CREATE INDEX idx_loan_book_id ON loan(book_id);
CREATE INDEX idx_loan_member_id ON loan(member_id);

-- =========================================
-- OPTIONAL: SEED CONFIGURATION (1 row)
-- =========================================
INSERT INTO configuration (
    max_active_loans,
    allow_member_to_borrow_when_overdue_loan,
    loan_due_days
) VALUES (
    3,
    FALSE,
    7
);