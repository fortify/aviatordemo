#!/bin/bash
ARTIFACT_ID=$(fcli ssc artifact ls --av "ghaviatordemo:main" --output json | 
jq -r '
    [.[] | select(.originalFileName | startswith("aviator_"))] | 
    sort_by(.uploadDate) | 
    last | 
    .id
')
echo "Latest aviator artifact ID: $ARTIFACT_ID"
fcli aviator ssc apply-remediations --artifact-id $ARTIFACT_ID