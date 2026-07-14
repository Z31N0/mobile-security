# cats_backend/models.py
from datetime import date
from typing import List
from pydantic import BaseModel


class AdoptedCat(BaseModel):
    id: int
    imageUrl: str


class UserProfile(BaseModel):
    id: int
    name: str
    birthdate: date
    email: str
    phone: str
    role: str
    adoptedCats: List[AdoptedCat]
