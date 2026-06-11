import { NextRequest, NextResponse } from 'next/server';
import { dataStore } from '@/lib/db/store';
import { signToken } from '@/lib/auth/jwt';
import { User, FamilyMember } from '@/types';
import { generateId } from '@/lib/utils';

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const { name, email, password, phone } = body;

    if (!name || !email || !password) {
      return NextResponse.json({ success: false, message: 'Missing required fields' }, { status: 400 });
    }

    // For demo purposes, we'll create a new mock user and update the store
    // Normally this creates records in the DB
    const userId = `user-${generateId()}`;
    const familyId = `family-${generateId()}`;
    const memberId = `fm-${generateId()}`;

    const newUser: User = {
      id: userId,
      email,
      name,
      phone,
      language: 'en',
      role: 'head',
      familyId: familyId,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    const newMember: FamilyMember = {
      id: memberId,
      userId: userId,
      name: name,
      relationship: 'self',
      dateOfBirth: '1990-01-01', // Default, would collect later
      gender: 'other',
      phone: phone,
      email: email,
      isActive: true,
      createdAt: new Date().toISOString(),
    };

    // Update store (this is ephemeral in memory for the demo)
    dataStore.updateUser(newUser);
    dataStore.addFamilyMember(newMember);

    const token = await signToken({
      userId: newUser.id,
      role: newUser.role,
      familyId: newUser.familyId,
    });

    const response = NextResponse.json({
      success: true,
      data: {
        user: newUser,
        token
      }
    });

    response.cookies.set({
      name: 'nidhi-token',
      value: token,
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      maxAge: 60 * 60 * 24 * 7,
      path: '/',
    });

    return response;
  } catch (error) {
    return NextResponse.json({ success: false, message: 'Internal server error' }, { status: 500 });
  }
}
