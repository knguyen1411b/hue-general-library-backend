/**
 * ENUMERATIONS
 */

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  BLOCKED = 'BLOCKED',
  DELETED = 'DELETED'
}

export enum UserRole {
  USER = 'USER',
  MANAGER = 'MANAGER',
  ADMIN = 'ADMIN'
}

export enum BookItemStatus {
  AVAILABLE = 'AVAILABLE',
  BORROWED = 'BORROWED',
  LOST = 'LOST',
  DAMAGED = 'DAMAGED',
  PENDING = 'PENDING'
}

export enum RentalStatus {
  BORROWING = 'BORROWING',
  RETURNED = 'RETURNED',
  OVERDUE = 'OVERDUE',
  LOST = 'LOST'
}

export enum FineStatus {
  UNPAID = 'UNPAID',
  PAID = 'PAID',
  CANCELED = 'CANCELED'
}

export enum PaymentType {
  FINE = 'FINE',
  SUBSCRIPTION = 'SUBSCRIPTION'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED'
}

export enum SubscriptionStatus {
  ACTIVE = 'ACTIVE',
  EXPIRED = 'EXPIRED',
  CANCELED = 'CANCELED'
}

export enum CardStatus {
  ACTIVE = 'ACTIVE',
  EXPIRED = 'EXPIRED',
  INACTIVE = 'INACTIVE',
  BLOCKED = 'BLOCKED',
  LOST = 'LOST'
}

/**
 * ENTITIES
 */

export interface User {
  id: string; // UUID
  fullName: string;
  email: string;
  phone: string;
  password?: string; // Thường không trả về trong API response
  birthday: string; // Date string
  gender: boolean;
  identityId: string;
  address: string;
  avatarUrl: string;
  identityFrontUrl: string;
  identityBackUrl: string;
  status: UserStatus;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

export interface LibraryCard {
  id: string; // UUID
  userId: string;
  issueDate: string;
  expiryDate: string;
  status: CardStatus;
  createdAt: string;
  updatedAt: string;
}

export interface Subscription {
  id: string; // UUID
  name: string;
  key: string;
  maxBooks: number;
  price: number;
  durationDays: number;
  overdueFeePerDay: number;
  maxRenewals: number;
  compensationRate: number;
}

export interface UserSubscription {
  id: string; // UUID
  userId: string;
  subscriptionId: string;
  startDate: string;
  endDate: string;
  status: SubscriptionStatus;
  maxBooks: number;
  price: number;
}

export interface Payment {
  id: string; // UUID
  userId: string;
  amount: number;
  paymentStatus: PaymentStatus;
  createdAt: string;
  paymentType: PaymentType;
  fineId?: string; // Optional tùy thuộc vào PaymentType
  userSubscriptionId?: string; // Optional tùy thuộc vào PaymentType
}

export interface Rental {
  id: string; // UUID
  userId: string;
  bookItemId: string;
  rentDate: string;
  dueDate: string;
  returnDate?: string;
  status: RentalStatus;
}

export interface Fine {
  id: string; // UUID
  rentalId: string;
  amount: number;
  reason: string;
  status: FineStatus;
}

export interface Category {
  id: string;
  title: string;
}

export interface Book {
  id: string; // UUID
  title: string;
  description: string;
  price: number;
  authors: string;
  publisher: string;
  count: number;
  createdAt: string;
  updatedAt: string;
  imgUrl: string;
  version: number;
  categoryId: string; // Tham chiếu từ "classifies"
}

export interface BookItem {
  id: string; // UUID
  bookId: string;
  status: BookItemStatus;
  barcode: string;
  importDate: string;
  shelfPositionId: string; // Tham chiếu từ "stores"
}

/**
 * LOCATION ENTITIES
 */

export interface Floor {
  id: number;
  name: string;
}

export interface Aisle {
  id: number;
  floorId: number;
  name: string;
}

export interface Shelf {
  id: number;
  aisleId: number;
  name: string;
  maxCol: number;
  maxRow: number;
}

export interface ShelfPosition {
  id: number;
  shelfId: number;
  rowIndex: number;
  columnIndex: number;
}