-- Create HNSW indexes for vector similarity search
CREATE INDEX idx_resumes_embedding_hnsw ON resumes 
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 200);

CREATE INDEX idx_jobs_embedding_hnsw ON job_postings 
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 200);

-- Create partial indexes for performance
CREATE INDEX idx_match_results_pending ON match_results(user_id)
WHERE status = 'PENDING';

CREATE INDEX idx_user_active ON users(is_active)
WHERE is_active = true;

-- Create composite indexes for common queries
CREATE INDEX idx_matches_user_job ON match_results(user_id, job_id);
CREATE INDEX idx_applications_user_job ON applications(user_id, job_id);
