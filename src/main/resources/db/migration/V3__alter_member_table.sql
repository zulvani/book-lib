ALTER TABLE public."member" ALTER COLUMN max_active_loans DROP NOT NULL;
ALTER TABLE public."member" ALTER COLUMN allow_member_to_borrow_when_overdue_loan DROP NOT NULL;
ALTER TABLE public."member" ALTER COLUMN loan_due_days DROP NOT NULL;
