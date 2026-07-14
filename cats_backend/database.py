from __future__ import annotations

from dataclasses import dataclass
from datetime import date
from typing import Dict, List, Optional

from models import AdoptedCat, UserProfile


@dataclass
class UserRecord:
    id: int
    username: str
    password: str   # plain text (intentionally insecure)
    role: str       # "owner" or "customer"
    profile: UserProfile


# In-memory database
_users_by_id: Dict[int, UserRecord] = {}
_users_by_username: Dict[str, UserRecord] = {}
_next_id: int = 1


def _add_user(
    *,
    username: str,
    password: str,
    role: str,
    name: str,
    birthdate: date,
    email: str,
    phone: str,
    adoptedCats: List[AdoptedCat],
) -> UserRecord:
    """Create a new user and store it in the in-memory DB."""
    global _next_id

    user_id = _next_id
    _next_id += 1

    profile = UserProfile(
        id=user_id,
        name=name,
        birthdate=birthdate,
        email=email,
        phone=phone,
        role=role,
        adoptedCats=adoptedCats,
    )

    record = UserRecord(
        id=user_id,
        username=username,
        password=password,
        role=role,
        profile=profile,
    )

    _users_by_id[user_id] = record
    _users_by_username[username] = record
    return record


def init_fake_data() -> None:
    """Seed initial demo users for IDOR."""
    global _users_by_id, _users_by_username, _next_id
    _users_by_id = {}
    _users_by_username = {}
    _next_id = 1

    # ---- Owner (ID = 1) ----
    _add_user(
        username="owner",
        password="owner123",
        role="owner",
        name="Ameer Shahpoor",
        birthdate=date(1970, 1, 1),
        email="support@catsadoption.shop",
        phone="+31 6 1234 5678",
        adoptedCats=[],
    )

    # ---- Demo victim 1 (ID = 2) ----
    _add_user(
        username="moestafa",
        password="moestafa",
        role="customer",
        name="Moestafa",
        birthdate=date(1992, 5, 10),
        email="moestafa@example.com",
        phone="+31 6 1111 1111",
        adoptedCats=[
            AdoptedCat(id=101, imageUrl="https://cdn2.thecatapi.com/images/a51.jpg"),
            AdoptedCat(id=102, imageUrl="https://cdn2.thecatapi.com/images/b1a.jpg"),
        ],
    )

    # ---- Demo victim 2 (ID = 3) ----
    _add_user(
        username="zeini",
        password="zeini",
        role="customer",
        name="Zeini Ddin",
        birthdate=date(1990, 9, 20),
        email="zeini@example.com",
        phone="+31 6 2222 2222",
        adoptedCats=[
            AdoptedCat(id=201, imageUrl="https://cdn2.thecatapi.com/images/b1a.jpg"),
        ],
    )

def get_user_by_id(user_id: int) -> Optional[UserRecord]:
    return _users_by_id.get(user_id)
