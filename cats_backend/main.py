from fastapi import FastAPI, HTTPException
from models import UserProfile, AdoptedCat
from database import init_fake_data, get_user_by_id
from datetime import date

app = FastAPI(title="CatsAdoption IDOR backend")

init_fake_data()

@app.get("/users/{user_id}", response_model=UserProfile)
def get_user(user_id: int):
    """Returns a user profile or an empty profile if not found."""
    record = get_user_by_id(user_id)
    if not record:
        # Instead of raising an error, return a valid empty profile.
        # This prevents the backend from crashing and the Android app from getting malformed JSON.
        return UserProfile(
            id=0,
            name="User Not Found",
            birthdate=date(1900, 1, 1),
            email="",
            phone="",
            role="customer",
            adoptedCats=[]
        )
    return record.profile
